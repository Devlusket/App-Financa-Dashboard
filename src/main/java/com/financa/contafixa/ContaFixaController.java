package com.financa.contafixa;

import com.financa.contafixa.dto.ContaFixaCreateRequest;
import com.financa.contafixa.dto.ContaFixaResponse;
import com.financa.contafixa.dto.ContaFixaUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contas-fixas")
@RequiredArgsConstructor
public class ContaFixaController {

    private final ContaFixaService contaFixaService;

    @GetMapping
    public List<ContaFixaResponse> listar() {
        return contaFixaService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContaFixaResponse criar(@Valid @RequestBody ContaFixaCreateRequest request) {
        return contaFixaService.criar(request);
    }

    @PatchMapping("/{id}")
    public ContaFixaResponse atualizarValor(@PathVariable UUID id, @Valid @RequestBody ContaFixaUpdateRequest request) {
        return contaFixaService.atualizarValor(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id) {
        contaFixaService.remover(id);
    }
}
