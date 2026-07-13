package com.financa.categoria.dto;

import com.financa.domain.enums.TipoDivisao;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public record CategoriaUpdateRequest(
        String nome,
        TipoDivisao tipoDivisao,
        UUID responsavelId,
        Boolean ehPoupanca,
        List<@Valid DivisaoPercentualRequest> divisoesPercentuais
) {
}
