package com.financa.pessoa.dto;

import jakarta.validation.constraints.NotBlank;

public record PessoaRequest(@NotBlank String nome) {
}
