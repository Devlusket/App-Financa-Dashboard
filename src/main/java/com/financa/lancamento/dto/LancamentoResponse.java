package com.financa.lancamento.dto;

import com.financa.domain.enums.StatusLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LancamentoResponse(
        UUID id,
        UUID categoriaId,
        String categoriaNome,
        String descricao,
        BigDecimal valor,
        LocalDate data,
        String mesReferencia,
        UUID responsavelPagamentoId,
        StatusLancamento status
) {
}
