package com.financa.categoria.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record DivisaoPercentualResponse(UUID pessoaId, BigDecimal percentual) {
}
