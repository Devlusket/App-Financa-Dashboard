package com.financa.renda.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RendaAdicionalResponse(UUID id, String descricao, BigDecimal valor) {
}
