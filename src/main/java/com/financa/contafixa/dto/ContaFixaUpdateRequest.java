package com.financa.contafixa.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ContaFixaUpdateRequest(@NotNull @DecimalMin("0.00") BigDecimal valorAtual) {
}
