package com.financa.renda.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.UUID;

public record RendaCreateRequest(
        @NotNull UUID pessoaId,
        @NotNull @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])$") String mesReferencia,
        @NotNull @DecimalMin("0.00") BigDecimal valorFixo
) {
}
