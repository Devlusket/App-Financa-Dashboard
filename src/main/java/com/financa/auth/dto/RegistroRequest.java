package com.financa.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RegistroRequest(
        @NotBlank String usuario,
        @NotBlank String senha,
        String nome
) {
}
