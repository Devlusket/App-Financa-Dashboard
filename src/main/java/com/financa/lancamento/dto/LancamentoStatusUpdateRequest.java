package com.financa.lancamento.dto;

import com.financa.domain.enums.StatusLancamento;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LancamentoStatusUpdateRequest(@NotNull StatusLancamento status, UUID responsavelPagamentoId) {
}
