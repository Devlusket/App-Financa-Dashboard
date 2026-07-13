package com.financa.contafixa;

import com.financa.contafixa.dto.ContaFixaCreateRequest;
import com.financa.contafixa.dto.ContaFixaResponse;
import com.financa.contafixa.dto.ContaFixaUpdateRequest;
import com.financa.domain.entity.Casa;
import com.financa.domain.entity.Categoria;
import com.financa.domain.entity.ContaFixa;
import com.financa.repository.CasaRepository;
import com.financa.repository.CategoriaRepository;
import com.financa.repository.ContaFixaRepository;
import com.financa.security.CasaContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContaFixaService {

    private final CasaContext casaContext;
    private final CasaRepository casaRepository;
    private final CategoriaRepository categoriaRepository;
    private final ContaFixaRepository contaFixaRepository;

    @Transactional(readOnly = true)
    public List<ContaFixaResponse> listar() {
        return contaFixaRepository.findAllByCasaIdOrderByNomeAsc(casaContext.getCasaId()).stream()
                .map(this::resposta)
                .toList();
    }

    @Transactional
    public ContaFixaResponse criar(ContaFixaCreateRequest request) {
        UUID casaId = casaContext.getCasaId();
        Casa casa = casaRepository.getReferenceById(casaId);
        Categoria categoria = categoriaRepository.findByIdAndCasaId(request.categoriaId(), casaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Categoria não pertence à casa autenticada"));
        ContaFixa contaFixa = new ContaFixa(casa, categoria, request.nome().trim(), request.valorAtual());
        return resposta(contaFixaRepository.save(contaFixa));
    }

    @Transactional
    public ContaFixaResponse atualizarValor(UUID contaFixaId, ContaFixaUpdateRequest request) {
        ContaFixa contaFixa = buscarDaCasa(contaFixaId);
        contaFixa.setValorAtual(request.valorAtual());
        return resposta(contaFixa);
    }

    @Transactional
    public void remover(UUID contaFixaId) {
        contaFixaRepository.delete(buscarDaCasa(contaFixaId));
    }

    private ContaFixa buscarDaCasa(UUID contaFixaId) {
        return contaFixaRepository.findByIdAndCasaId(contaFixaId, casaContext.getCasaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Conta fixa não encontrada: " + contaFixaId));
    }

    private ContaFixaResponse resposta(ContaFixa contaFixa) {
        return new ContaFixaResponse(
                contaFixa.getId(),
                contaFixa.getCategoria().getId(),
                contaFixa.getNome(),
                contaFixa.getValorAtual()
        );
    }
}
