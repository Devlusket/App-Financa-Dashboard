package com.financa.lancamento;

import com.financa.lancamento.dto.LancamentoCreateRequest;
import com.financa.lancamento.dto.LancamentoResponse;
import com.financa.lancamento.dto.LancamentoStatusUpdateRequest;
import com.financa.lancamento.dto.LancamentoUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lancamentos")
@RequiredArgsConstructor
@Validated
public class LancamentoController {

    private final LancamentoService lancamentoService;

    /** pessoaId filtra por quem pagou fisicamente (responsavelPagamento), não pela divisão financeira. */
    @GetMapping
    public List<LancamentoResponse> listar(
            @RequestParam(required = false) @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])$") String mes,
            @RequestParam(required = false) UUID pessoaId,
            @RequestParam(required = false) UUID categoriaId
    ) {
        return lancamentoService.listar(mes, pessoaId, categoriaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LancamentoResponse criar(@Valid @RequestBody LancamentoCreateRequest request) {
        return lancamentoService.criar(request);
    }

    @PatchMapping("/{id}")
    public LancamentoResponse atualizar(@PathVariable UUID id, @Valid @RequestBody LancamentoUpdateRequest request) {
        return lancamentoService.atualizar(id, request);
    }

    @PatchMapping("/{id}/status")
    public LancamentoResponse atualizarStatus(
            @PathVariable UUID id,
            @Valid @RequestBody LancamentoStatusUpdateRequest request
    ) {
        return lancamentoService.atualizarStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id) {
        lancamentoService.remover(id);
    }
}
