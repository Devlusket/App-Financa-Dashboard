package com.financa.contafixa.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaFixaResponse(UUID id, UUID categoriaId, String nome, BigDecimal valorAtual) {
}
