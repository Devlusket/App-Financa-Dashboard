package com.financa.lancamento.dto;

import com.financa.domain.enums.StatusLancamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LancamentoCreateRequest(
        @NotNull UUID categoriaId,
        @NotBlank String descricao,
        @NotNull @DecimalMin("0.00") BigDecimal valor,
        @NotNull LocalDate data,
        UUID responsavelPagamentoId,
        StatusLancamento status
) {
}
