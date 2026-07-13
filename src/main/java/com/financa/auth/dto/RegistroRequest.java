package com.financa.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistroRequest(
        @NotBlank @Email String email,
        @NotBlank String senha,
        String nome
) {
}
