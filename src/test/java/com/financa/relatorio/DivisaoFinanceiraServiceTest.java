package com.financa.relatorio;

import com.financa.domain.enums.TipoDivisao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DivisaoFinanceiraServiceTest {

    private final DivisaoFinanceiraService service = new DivisaoFinanceiraService();
    private final UUID pessoa1 = UUID.randomUUID();
    private final UUID pessoa2 = UUID.randomUUID();

    @Test
    void atribuiTodoValorAoResponsavelNaDivisaoFixaPorPessoa() {
        Map<UUID, BigDecimal> resultado = service.calcular(
                TipoDivisao.FIXO_POR_PESSOA, pessoa1, List.of(), List.of(pessoa1, pessoa2), new BigDecimal("100.00")
        );

        assertEquals(Map.of(pessoa1, new BigDecimal("100.00")), resultado);
    }

    @Test
    void divideValorConformePercentuaisVinculadosAsPessoas() {
        Map<UUID, BigDecimal> resultado = service.calcular(
                TipoDivisao.PERCENTUAL,
                null,
                List.of(
                        new DivisaoFinanceiraService.DivisaoPercentual(pessoa1, new BigDecimal("60")),
                        new DivisaoFinanceiraService.DivisaoPercentual(pessoa2, new BigDecimal("40"))
                ),
                List.of(pessoa1, pessoa2),
                new BigDecimal("100.00")
        );

        assertEquals(new BigDecimal("60.0000"), resultado.get(pessoa1));
        assertEquals(new BigDecimal("40.0000"), resultado.get(pessoa2));
    }

    @Test
    void divideIgualmenteEntreAsPessoasDaCasa() {
        Map<UUID, BigDecimal> resultado = service.calcular(
                TipoDivisao.VALOR_FIXO_DIVIDIDO, null, List.of(), List.of(pessoa1, pessoa2), new BigDecimal("100.00")
        );

        assertEquals(new BigDecimal("50.00"), resultado.get(pessoa1));
        assertEquals(new BigDecimal("50.00"), resultado.get(pessoa2));
    }
}
