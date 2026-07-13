package com.financa.relatorio;

import com.financa.domain.entity.CategoriaDivisao;
import com.financa.domain.entity.Lancamento;
import com.financa.domain.entity.Pessoa;
import com.financa.domain.entity.Renda;
import com.financa.relatorio.dto.GastoPorCategoriaResponse;
import com.financa.relatorio.dto.RelatorioCasaResponse;
import com.financa.relatorio.dto.RelatorioMensalResponse;
import com.financa.relatorio.dto.RelatorioPessoaResponse;
import com.financa.repository.LancamentoRepository;
import com.financa.repository.PessoaRepository;
import com.financa.repository.RendaRepository;
import com.financa.security.CasaContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RelatorioMensalService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final CasaContext casaContext;
    private final PessoaRepository pessoaRepository;
    private final RendaRepository rendaRepository;
    private final LancamentoRepository lancamentoRepository;
    private final DivisaoFinanceiraService divisaoFinanceiraService;

    @Transactional(readOnly = true)
    public RelatorioMensalResponse gerar(String mesReferencia) {
        UUID casaId = casaContext.getCasaId();
        List<Pessoa> pessoas = pessoaRepository.findAllByCasaIdOrderByNomeAsc(casaId);
        Map<UUID, AcumuladoPessoa> acumulados = new LinkedHashMap<>();
        List<UUID> pessoaIds = pessoas.stream().map(Pessoa::getId).toList();
        pessoas.forEach(pessoa -> acumulados.put(pessoa.getId(), new AcumuladoPessoa(pessoa)));

        rendaRepository.findAllByPessoaCasaIdAndMesReferencia(casaId, mesReferencia).forEach(renda ->
                acumulados.get(renda.getPessoa().getId()).renda = acumulados.get(renda.getPessoa().getId()).renda
                        .add(totalRenda(renda))
        );

        Map<UUID, TotalCategoria> gastosPorCategoria = new HashMap<>();
        lancamentoRepository.findAllByCasaIdAndMesReferencia(casaId, mesReferencia).forEach(lancamento -> {
            Map<UUID, BigDecimal> valoresPorPessoa = divisaoFinanceiraService.calcular(
                    lancamento.getCategoria().getTipoDivisao(),
                    lancamento.getCategoria().getResponsavel() == null ? null : lancamento.getCategoria().getResponsavel().getId(),
                    lancamento.getCategoria().getDivisoesPercentuais().stream()
                            .map(divisao -> new DivisaoFinanceiraService.DivisaoPercentual(
                                    divisao.getPessoa().getId(), divisao.getPercentual()
                            )).toList(),
                    pessoaIds,
                    lancamento.getValor()
            );

            valoresPorPessoa.forEach((pessoaId, valor) -> {
                AcumuladoPessoa acumulado = acumulados.get(pessoaId);
                if (lancamento.getCategoria().isEhPoupanca()) {
                    acumulado.guardado = acumulado.guardado.add(valor);
                } else {
                    acumulado.gasto = acumulado.gasto.add(valor);
                }
            });

            if (!lancamento.getCategoria().isEhPoupanca()) {
                gastosPorCategoria.compute(lancamento.getCategoria().getId(), (categoriaId, totalAtual) -> {
                    if (totalAtual == null) {
                        return new TotalCategoria(lancamento.getCategoria().getNome(), lancamento.getValor());
                    }
                    totalAtual.total = totalAtual.total.add(lancamento.getValor());
                    return totalAtual;
                });
            }
        });

        List<RelatorioPessoaResponse> porPessoa = acumulados.values().stream()
                .map(AcumuladoPessoa::resposta)
                .toList();
        BigDecimal rendaCasa = porPessoa.stream().map(RelatorioPessoaResponse::renda).reduce(ZERO, BigDecimal::add);
        BigDecimal gastoCasa = porPessoa.stream().map(RelatorioPessoaResponse::gasto).reduce(ZERO, BigDecimal::add);
        BigDecimal guardadoCasa = porPessoa.stream().map(RelatorioPessoaResponse::guardado).reduce(ZERO, BigDecimal::add);
        RelatorioCasaResponse casa = new RelatorioCasaResponse(
                rendaCasa, gastoCasa, guardadoCasa, rendaCasa.subtract(gastoCasa).subtract(guardadoCasa)
        );

        List<GastoPorCategoriaResponse> categorias = gastosPorCategoria.entrySet().stream()
                .map(entry -> new GastoPorCategoriaResponse(entry.getKey(), entry.getValue().nome, entry.getValue().total))
                .sorted(Comparator.comparing(GastoPorCategoriaResponse::nome))
                .toList();
        return new RelatorioMensalResponse(mesReferencia, porPessoa, casa, categorias);
    }

    private BigDecimal totalRenda(Renda renda) {
        return renda.getAdicionais().stream().map(adicional -> adicional.getValor())
                .reduce(renda.getValorFixo(), BigDecimal::add);
    }

    private static class AcumuladoPessoa {
        private final Pessoa pessoa;
        private BigDecimal renda = ZERO;
        private BigDecimal gasto = ZERO;
        private BigDecimal guardado = ZERO;

        private AcumuladoPessoa(Pessoa pessoa) {
            this.pessoa = pessoa;
        }

        private RelatorioPessoaResponse resposta() {
            return new RelatorioPessoaResponse(
                    pessoa.getId(), pessoa.getNome(), renda, gasto, guardado, renda.subtract(gasto).subtract(guardado)
            );
        }
    }

    private static class TotalCategoria {
        private final String nome;
        private BigDecimal total;

        private TotalCategoria(String nome, BigDecimal total) {
            this.nome = nome;
            this.total = total;
        }
    }
}
