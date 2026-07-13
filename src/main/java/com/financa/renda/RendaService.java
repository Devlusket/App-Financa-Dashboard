package com.financa.renda;

import com.financa.domain.entity.Pessoa;
import com.financa.domain.entity.Renda;
import com.financa.domain.entity.RendaAdicional;
import com.financa.renda.dto.RendaAdicionalCreateRequest;
import com.financa.renda.dto.RendaAdicionalResponse;
import com.financa.renda.dto.RendaConsultaResponse;
import com.financa.renda.dto.RendaCreateRequest;
import com.financa.renda.dto.RendaUpdateRequest;
import com.financa.repository.PessoaRepository;
import com.financa.repository.RendaAdicionalRepository;
import com.financa.repository.RendaRepository;
import com.financa.security.CasaContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RendaService {

    private final CasaContext casaContext;
    private final PessoaRepository pessoaRepository;
    private final RendaRepository rendaRepository;
    private final RendaAdicionalRepository rendaAdicionalRepository;

    @Transactional(readOnly = true)
    public RendaConsultaResponse consultar(UUID pessoaId, String mesReferencia) {
        UUID casaId = casaContext.getCasaId();
        buscarPessoaDaCasa(pessoaId, casaId);

        return rendaRepository.findByPessoaIdAndMesReferencia(pessoaId, mesReferencia)
                .map(this::respostaExistente)
                .orElseGet(() -> respostaAusente(pessoaId, mesReferencia));
    }

    @Transactional
    public RendaConsultaResponse criar(RendaCreateRequest request) {
        UUID casaId = casaContext.getCasaId();
        Pessoa pessoa = buscarPessoaDaCasa(request.pessoaId(), casaId);
        if (rendaRepository.findByPessoaIdAndMesReferencia(request.pessoaId(), request.mesReferencia()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe renda para essa pessoa neste mês");
        }

        Renda renda = rendaRepository.save(new Renda(pessoa, request.mesReferencia(), request.valorFixo()));
        return respostaExistente(renda);
    }

    @Transactional
    public RendaConsultaResponse atualizar(UUID rendaId, RendaUpdateRequest request) {
        Renda renda = buscarRendaDaCasa(rendaId);
        renda.setValorFixo(request.valorFixo());
        return respostaExistente(renda);
    }

    @Transactional
    public RendaAdicionalResponse adicionar(UUID rendaId, RendaAdicionalCreateRequest request) {
        Renda renda = buscarRendaDaCasa(rendaId);
        RendaAdicional adicional = rendaAdicionalRepository.save(
                new RendaAdicional(renda, request.descricao().trim(), request.valor())
        );
        return resposta(adicional);
    }

    @Transactional
    public void removerAdicional(UUID adicionalId) {
        RendaAdicional adicional = rendaAdicionalRepository.findByIdAndRendaPessoaCasaId(adicionalId, casaContext.getCasaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Renda adicional não encontrada: " + adicionalId));
        rendaAdicionalRepository.delete(adicional);
    }

    private RendaConsultaResponse respostaAusente(UUID pessoaId, String mesReferencia) {
        BigDecimal sugestao = rendaRepository
                .findTopByPessoaIdAndMesReferenciaLessThanOrderByMesReferenciaDesc(pessoaId, mesReferencia)
                .map(Renda::getValorFixo)
                .orElse(null);
        return new RendaConsultaResponse(false, null, pessoaId, mesReferencia, null, List.of(), sugestao);
    }

    private RendaConsultaResponse respostaExistente(Renda renda) {
        List<RendaAdicionalResponse> adicionais = renda.getAdicionais().stream().map(this::resposta).toList();
        return new RendaConsultaResponse(
                true,
                renda.getId(),
                renda.getPessoa().getId(),
                renda.getMesReferencia(),
                renda.getValorFixo(),
                adicionais,
                null
        );
    }

    private RendaAdicionalResponse resposta(RendaAdicional adicional) {
        return new RendaAdicionalResponse(adicional.getId(), adicional.getDescricao(), adicional.getValor());
    }

    private Pessoa buscarPessoaDaCasa(UUID pessoaId, UUID casaId) {
        return pessoaRepository.findByIdAndCasaId(pessoaId, casaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Pessoa não pertence à casa autenticada"));
    }

    private Renda buscarRendaDaCasa(UUID rendaId) {
        return rendaRepository.findByIdAndPessoaCasaId(rendaId, casaContext.getCasaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Renda não encontrada: " + rendaId));
    }
}
