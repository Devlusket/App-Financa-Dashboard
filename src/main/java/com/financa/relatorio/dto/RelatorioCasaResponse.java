package com.financa.relatorio.dto;

import java.math.BigDecimal;

public record RelatorioCasaResponse(BigDecimal renda, BigDecimal gasto, BigDecimal guardado, BigDecimal saldo) {
}
