package com.financa.renda.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RendaAdicionalCreateRequest(
        @NotBlank String descricao,
        @NotNull @DecimalMin("0.00") BigDecimal valor
) {
}
