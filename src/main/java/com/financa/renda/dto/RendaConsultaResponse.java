package com.financa.renda.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record RendaConsultaResponse(
        boolean existe,
        UUID id,
        UUID pessoaId,
        String mesReferencia,
        BigDecimal valorFixo,
        List<RendaAdicionalResponse> adicionais,
        BigDecimal valorFixoSugerido
) {
}
