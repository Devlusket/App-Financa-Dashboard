package com.financa.pessoa;

import com.financa.pessoa.dto.PessoaRequest;
import com.financa.pessoa.dto.PessoaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pessoas")
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaService pessoaService;

    @GetMapping
    public List<PessoaResponse> listar() {
        return pessoaService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PessoaResponse criar(@Valid @RequestBody PessoaRequest request) {
        return pessoaService.criar(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id) {
        pessoaService.remover(id);
    }
}
