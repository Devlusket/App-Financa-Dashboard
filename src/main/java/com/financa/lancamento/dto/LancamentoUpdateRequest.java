package com.financa.lancamento.dto;

import com.financa.domain.enums.StatusLancamento;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LancamentoUpdateRequest(
        UUID categoriaId,
        String descricao,
        @DecimalMin("0.00") BigDecimal valor,
        LocalDate data,
        UUID responsavelPagamentoId,
        StatusLancamento status
) {
}
