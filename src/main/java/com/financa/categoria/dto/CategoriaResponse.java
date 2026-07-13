package com.financa.categoria.dto;

import com.financa.domain.enums.TipoDivisao;

import java.util.List;
import java.util.UUID;

public record CategoriaResponse(
        UUID id,
        String nome,
        TipoDivisao tipoDivisao,
        UUID responsavelId,
        boolean ehPoupanca,
        List<DivisaoPercentualResponse> divisoesPercentuais
) {
}
