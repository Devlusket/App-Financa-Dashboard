package com.financa.relatorio.dto;

import java.util.List;

public record RelatorioMensalResponse(
        String mesReferencia,
        List<RelatorioPessoaResponse> porPessoa,
        RelatorioCasaResponse casa,
        List<GastoPorCategoriaResponse> gastosPorCategoria,
        List<ValorPorCategoriaResponse> guardadoPorCategoria
) {
}
