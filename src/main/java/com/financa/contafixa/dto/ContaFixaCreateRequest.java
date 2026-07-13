package com.financa.contafixa.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaFixaCreateRequest(
        @NotNull UUID categoriaId,
        @NotBlank String nome,
        @NotNull @DecimalMin("0.00") BigDecimal valorAtual
) {
}
