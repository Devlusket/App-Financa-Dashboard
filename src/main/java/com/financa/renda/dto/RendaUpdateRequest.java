package com.financa.renda.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RendaUpdateRequest(@NotNull @DecimalMin("0.00") BigDecimal valorFixo) {
}
