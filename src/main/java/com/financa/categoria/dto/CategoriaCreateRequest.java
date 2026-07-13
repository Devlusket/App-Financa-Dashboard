package com.financa.categoria.dto;

import com.financa.domain.enums.TipoDivisao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CategoriaCreateRequest(
        @NotBlank String nome,
        @NotNull TipoDivisao tipoDivisao,
        UUID responsavelId,
        boolean ehPoupanca,
        List<@Valid DivisaoPercentualRequest> divisoesPercentuais
) {
}
