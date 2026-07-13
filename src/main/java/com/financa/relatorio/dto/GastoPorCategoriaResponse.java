package com.financa.relatorio.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record GastoPorCategoriaResponse(UUID categoriaId, String nome, BigDecimal total) {
}
