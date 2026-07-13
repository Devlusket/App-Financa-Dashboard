package com.financa.pessoa;

import com.financa.domain.entity.Casa;
import com.financa.domain.entity.Pessoa;
import com.financa.pessoa.dto.PessoaRequest;
import com.financa.pessoa.dto.PessoaResponse;
import com.financa.repository.CasaRepository;
import com.financa.repository.CategoriaDivisaoRepository;
import com.financa.repository.CategoriaRepository;
import com.financa.repository.LancamentoRepository;
import com.financa.repository.PessoaRepository;
import com.financa.repository.RendaRepository;
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
public class PessoaService {

    private static final int MAXIMO_PESSOAS_POR_CASA = 2;

    private final CasaContext casaContext;
    private final CasaRepository casaRepository;
    private final PessoaRepository pessoaRepository;
    private final CategoriaRepository categoriaRepository;
    private final CategoriaDivisaoRepository categoriaDivisaoRepository;
    private final RendaRepository rendaRepository;
    private final LancamentoRepository lancamentoRepository;

    @Transactional(readOnly = true)
    public List<PessoaResponse> listar() {
        return pessoaRepository.findAllByCasaIdOrderByNomeAsc(casaContext.getCasaId()).stream()
                .map(this::resposta)
                .toList();
    }

    @Transactional
    public PessoaResponse criar(PessoaRequest request) {
        UUID casaId = casaContext.getCasaId();
        if (pessoaRepository.countByCasaId(casaId) >= MAXIMO_PESSOAS_POR_CASA) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Uma casa pode ter no máximo duas pessoas");
        }

        Casa casa = casaRepository.getReferenceById(casaId);
        Pessoa pessoa = pessoaRepository.save(new Pessoa(casa, request.nome().trim()));
        return resposta(pessoa);
    }

    @Transactional
    public void remover(UUID pessoaId) {
        Pessoa pessoa = pessoaRepository.findByIdAndCasaId(pessoaId, casaContext.getCasaId())
                .orElseThrow(() -> naoEncontrada(pessoaId));

        if (categoriaRepository.existsByResponsavelId(pessoaId)
                || categoriaDivisaoRepository.existsByPessoaId(pessoaId)
                || rendaRepository.existsByPessoaId(pessoaId)
                || lancamentoRepository.existsByResponsavelPagamentoId(pessoaId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível remover pessoa com categorias, divisões, rendas ou lançamentos vinculados");
        }
        pessoaRepository.delete(pessoa);
    }

    private PessoaResponse resposta(Pessoa pessoa) {
        return new PessoaResponse(pessoa.getId(), pessoa.getNome());
    }

    private ResponseStatusException naoEncontrada(UUID pessoaId) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa não encontrada: " + pessoaId);
    }
}
