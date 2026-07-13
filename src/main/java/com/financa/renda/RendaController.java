package com.financa.renda;

import com.financa.renda.dto.RendaAdicionalCreateRequest;
import com.financa.renda.dto.RendaAdicionalResponse;
import com.financa.renda.dto.RendaConsultaResponse;
import com.financa.renda.dto.RendaCreateRequest;
import com.financa.renda.dto.RendaUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/rendas")
@RequiredArgsConstructor
@Validated
public class RendaController {

    private final RendaService rendaService;

    @GetMapping
    public RendaConsultaResponse consultar(
            @RequestParam @NotNull UUID pessoaId,
            @RequestParam("mes") @NotNull @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])$") String mes
    ) {
        return rendaService.consultar(pessoaId, mes);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RendaConsultaResponse criar(@Valid @RequestBody RendaCreateRequest request) {
        return rendaService.criar(request);
    }

    @PatchMapping("/{id}")
    public RendaConsultaResponse atualizar(@PathVariable UUID id, @Valid @RequestBody RendaUpdateRequest request) {
        return rendaService.atualizar(id, request);
    }

    @PostMapping("/{id}/adicionais")
    @ResponseStatus(HttpStatus.CREATED)
    public RendaAdicionalResponse adicionar(@PathVariable UUID id, @Valid @RequestBody RendaAdicionalCreateRequest request) {
        return rendaService.adicionar(id, request);
    }

    @DeleteMapping("/adicionais/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerAdicional(@PathVariable UUID id) {
        rendaService.removerAdicional(id);
    }
}
