package com.financa.categoria;

import com.financa.categoria.dto.CategoriaCreateRequest;
import com.financa.categoria.dto.CategoriaResponse;
import com.financa.categoria.dto.CategoriaUpdateRequest;
import com.financa.categoria.dto.DivisaoPercentualRequest;
import com.financa.categoria.dto.DivisaoPercentualResponse;
import com.financa.domain.entity.Casa;
import com.financa.domain.entity.Categoria;
import com.financa.domain.entity.CategoriaDivisao;
import com.financa.domain.entity.Pessoa;
import com.financa.domain.enums.TipoDivisao;
import com.financa.repository.CasaRepository;
import com.financa.repository.CategoriaRepository;
import com.financa.repository.ContaFixaRepository;
import com.financa.repository.LancamentoRepository;
import com.financa.repository.PessoaRepository;
import com.financa.security.CasaContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CasaContext casaContext;
    private final CasaRepository casaRepository;
    private final CategoriaRepository categoriaRepository;
    private final PessoaRepository pessoaRepository;
    private final ContaFixaRepository contaFixaRepository;
    private final LancamentoRepository lancamentoRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAllByCasaIdOrderByNomeAsc(casaContext.getCasaId()).stream()
                .map(this::resposta)
                .toList();
    }

    @Transactional
    public CategoriaResponse criar(CategoriaCreateRequest request) {
        UUID casaId = casaContext.getCasaId();
        Casa casa = casaRepository.getReferenceById(casaId);
        Categoria categoria = new Categoria(casa, request.nome().trim(), request.tipoDivisao(), request.ehPoupanca());
        configurarDivisao(categoria, request.tipoDivisao(), request.responsavelId(), request.divisoesPercentuais(), casaId, true);
        return resposta(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse atualizar(UUID categoriaId, CategoriaUpdateRequest request) {
        UUID casaId = casaContext.getCasaId();
        Categoria categoria = buscarDaCasa(categoriaId, casaId);
        TipoDivisao tipoFinal = request.tipoDivisao() == null ? categoria.getTipoDivisao() : request.tipoDivisao();

        if (request.nome() != null) {
            if (request.nome().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da categoria não pode ser vazio");
            }
            categoria.setNome(request.nome().trim());
        }
        if (request.ehPoupanca() != null) {
            categoria.setEhPoupanca(request.ehPoupanca());
        }

        configurarDivisao(categoria, tipoFinal, request.responsavelId(), request.divisoesPercentuais(), casaId, false);
        categoria.setTipoDivisao(tipoFinal);
        return resposta(categoria);
    }

    @Transactional
    public void remover(UUID categoriaId) {
        Categoria categoria = buscarDaCasa(categoriaId, casaContext.getCasaId());
        if (lancamentoRepository.existsByCategoriaId(categoriaId)
                || contaFixaRepository.existsByCategoriaId(categoriaId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível remover categoria com lançamentos ou contas fixas vinculados");
        }
        categoriaRepository.delete(categoria);
    }

    private void configurarDivisao(
            Categoria categoria,
            TipoDivisao tipo,
            UUID responsavelId,
            List<DivisaoPercentualRequest> divisoes,
            UUID casaId,
            boolean criando
    ) {
        if (tipo == TipoDivisao.FIXO_POR_PESSOA) {
            UUID responsavelEfetivo = responsavelId != null
                    ? responsavelId
                    : categoria.getResponsavel() == null ? null : categoria.getResponsavel().getId();
            if (responsavelEfetivo == null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "responsavelId é obrigatório para FIXO_POR_PESSOA");
            }
            categoria.setResponsavel(buscarPessoaDaCasa(responsavelEfetivo, casaId));
            limparDivisoes(categoria);
            return;
        }

        categoria.setResponsavel(null);
        if (tipo == TipoDivisao.VALOR_FIXO_DIVIDIDO) {
            limparDivisoes(categoria);
            return;
        }

        if (divisoes != null) {
            substituirDivisoes(categoria, divisoes, casaId);
        }
        if (categoria.getDivisoesPercentuais().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "divisoesPercentuais é obrigatória para PERCENTUAL");
        }
        validarSomaPercentuais(categoria.getDivisoesPercentuais());
    }

    private void substituirDivisoes(Categoria categoria, List<DivisaoPercentualRequest> divisoes, UUID casaId) {
        if (divisoes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Uma categoria percentual precisa de ao menos uma divisão");
        }
        Set<UUID> pessoas = new HashSet<>();
        List<CategoriaDivisao> novasDivisoes = divisoes.stream().map(divisao -> {
            if (!pessoas.add(divisao.pessoaId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pessoa repetida nas divisões percentuais");
            }
            return new CategoriaDivisao(categoria, buscarPessoaDaCasa(divisao.pessoaId(), casaId), divisao.percentual());
        }).toList();
        validarSomaPercentuais(novasDivisoes);
        limparDivisoes(categoria);
        categoria.getDivisoesPercentuais().addAll(novasDivisoes);
    }

    private void validarSomaPercentuais(List<CategoriaDivisao> divisoes) {
        BigDecimal total = divisoes.stream()
                .map(CategoriaDivisao::getPercentual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(new BigDecimal("100.00")) != 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "A soma das divisões percentuais deve ser exatamente 100");
        }
    }

    private void limparDivisoes(Categoria categoria) {
        categoria.getDivisoesPercentuais().clear();
    }

    private Categoria buscarDaCasa(UUID categoriaId, UUID casaId) {
        return categoriaRepository.findByIdAndCasaId(categoriaId, casaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada: " + categoriaId));
    }

    private Pessoa buscarPessoaDaCasa(UUID pessoaId, UUID casaId) {
        return pessoaRepository.findByIdAndCasaId(pessoaId, casaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Pessoa não pertence à casa autenticada: " + pessoaId));
    }

    private CategoriaResponse resposta(Categoria categoria) {
        List<DivisaoPercentualResponse> divisoes = categoria.getDivisoesPercentuais().stream()
                .map(divisao -> new DivisaoPercentualResponse(divisao.getPessoa().getId(), divisao.getPercentual()))
                .toList();
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getTipoDivisao(),
                categoria.getResponsavel() == null ? null : categoria.getResponsavel().getId(),
                categoria.isEhPoupanca(),
                divisoes
        );
    }
}
