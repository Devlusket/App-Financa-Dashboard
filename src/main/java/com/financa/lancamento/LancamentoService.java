package com.financa.lancamento;

import com.financa.domain.entity.Casa;
import com.financa.domain.entity.Categoria;
import com.financa.domain.entity.Lancamento;
import com.financa.domain.entity.Pessoa;
import com.financa.domain.enums.StatusLancamento;
import com.financa.lancamento.dto.LancamentoCreateRequest;
import com.financa.lancamento.dto.LancamentoResponse;
import com.financa.lancamento.dto.LancamentoStatusUpdateRequest;
import com.financa.lancamento.dto.LancamentoUpdateRequest;
import com.financa.repository.CasaRepository;
import com.financa.repository.CategoriaRepository;
import com.financa.repository.LancamentoRepository;
import com.financa.repository.PessoaRepository;
import com.financa.security.CasaContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LancamentoService {

    private final CasaContext casaContext;
    private final CasaRepository casaRepository;
    private final CategoriaRepository categoriaRepository;
    private final PessoaRepository pessoaRepository;
    private final LancamentoRepository lancamentoRepository;

    @Transactional(readOnly = true)
    public List<LancamentoResponse> listar(String mes, UUID pessoaId, UUID categoriaId) {
        UUID casaId = casaContext.getCasaId();
        if (pessoaId != null) {
            buscarPessoaDaCasa(pessoaId, casaId);
        }
        if (categoriaId != null) {
            buscarCategoriaDaCasa(categoriaId, casaId);
        }

        Specification<Lancamento> filtro = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("casa").get("id"), casaId);
        if (mes != null) {
            filtro = filtro.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("mesReferencia"), mes));
        }
        if (pessoaId != null) {
            UUID filtroPessoaId = pessoaId;
            filtro = filtro.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("responsavelPagamento").get("id"), filtroPessoaId));
        }
        if (categoriaId != null) {
            UUID filtroCategoriaId = categoriaId;
            filtro = filtro.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("categoria").get("id"), filtroCategoriaId));
        }

        return lancamentoRepository.findAll(filtro, Sort.by(Sort.Direction.DESC, "data").and(Sort.by("id"))).stream()
                .map(this::resposta)
                .toList();
    }

    @Transactional
    public LancamentoResponse criar(LancamentoCreateRequest request) {
        UUID casaId = casaContext.getCasaId();
        Casa casa = casaRepository.getReferenceById(casaId);
        Categoria categoria = buscarCategoriaDaCasa(request.categoriaId(), casaId);
        Pessoa responsavelPagamento = request.responsavelPagamentoId() == null
                ? null
                : buscarPessoaDaCasa(request.responsavelPagamentoId(), casaId);
        Lancamento lancamento = new Lancamento(
                casa,
                categoria,
                request.descricao().trim(),
                request.valor(),
                request.data(),
                responsavelPagamento,
                request.status()
        );
        return resposta(lancamentoRepository.save(lancamento));
    }

    @Transactional
    public LancamentoResponse atualizar(UUID lancamentoId, LancamentoUpdateRequest request) {
        UUID casaId = casaContext.getCasaId();
        Lancamento lancamento = buscarDaCasa(lancamentoId, casaId);

        if (request.categoriaId() != null) {
            lancamento.setCategoria(buscarCategoriaDaCasa(request.categoriaId(), casaId));
        }
        if (request.descricao() != null) {
            if (request.descricao().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrição não pode ser vazia");
            }
            lancamento.setDescricao(request.descricao().trim());
        }
        if (request.valor() != null) {
            lancamento.setValor(request.valor());
        }
        if (request.data() != null) {
            atualizarDataEMes(lancamento, request.data());
        }
        if (request.responsavelPagamentoId() != null) {
            lancamento.setResponsavelPagamento(buscarPessoaDaCasa(request.responsavelPagamentoId(), casaId));
        }
        if (request.status() != null) {
            lancamento.setStatus(request.status());
        }
        return resposta(lancamento);
    }

    @Transactional
    public LancamentoResponse atualizarStatus(UUID lancamentoId, LancamentoStatusUpdateRequest request) {
        UUID casaId = casaContext.getCasaId();
        Lancamento lancamento = buscarDaCasa(lancamentoId, casaId);
        lancamento.setStatus(request.status());
        if (request.responsavelPagamentoId() != null) {
            lancamento.setResponsavelPagamento(buscarPessoaDaCasa(request.responsavelPagamentoId(), casaId));
        }
        return resposta(lancamento);
    }

    @Transactional
    public void remover(UUID lancamentoId) {
        lancamentoRepository.delete(buscarDaCasa(lancamentoId, casaContext.getCasaId()));
    }

    private void atualizarDataEMes(Lancamento lancamento, LocalDate data) {
        lancamento.setData(data);
        lancamento.setMesReferencia(data.getYear() + "-" + String.format("%02d", data.getMonthValue()));
    }

    private Lancamento buscarDaCasa(UUID lancamentoId, UUID casaId) {
        return lancamentoRepository.findByIdAndCasaId(lancamentoId, casaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Lançamento não encontrado: " + lancamentoId));
    }

    private Categoria buscarCategoriaDaCasa(UUID categoriaId, UUID casaId) {
        return categoriaRepository.findByIdAndCasaId(categoriaId, casaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Categoria não pertence à casa autenticada"));
    }

    private Pessoa buscarPessoaDaCasa(UUID pessoaId, UUID casaId) {
        return pessoaRepository.findByIdAndCasaId(pessoaId, casaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Pessoa não pertence à casa autenticada"));
    }

    private LancamentoResponse resposta(Lancamento lancamento) {
        return new LancamentoResponse(
                lancamento.getId(),
                lancamento.getCategoria().getId(),
                lancamento.getCategoria().getNome(),
                lancamento.getDescricao(),
                lancamento.getValor(),
                lancamento.getData(),
                lancamento.getMesReferencia(),
                lancamento.getResponsavelPagamento() == null ? null : lancamento.getResponsavelPagamento().getId(),
                lancamento.getStatus()
        );
    }
}
