package com.financa.relatorio.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RelatorioPessoaResponse(
        UUID pessoaId,
        String nome,
        BigDecimal renda,
        BigDecimal gasto,
        BigDecimal guardado,
        BigDecimal saldo
) {
}
