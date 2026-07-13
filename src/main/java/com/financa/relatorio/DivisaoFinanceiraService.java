package com.financa.relatorio;

import com.financa.domain.enums.TipoDivisao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DivisaoFinanceiraService {

    public Map<UUID, BigDecimal> calcular(
            TipoDivisao tipo,
            UUID responsavelId,
            List<DivisaoPercentual> divisoesPercentuais,
            List<UUID> pessoasDaCasa,
            BigDecimal valor
    ) {
        Map<UUID, BigDecimal> resultado = new LinkedHashMap<>();
        switch (tipo) {
            case FIXO_POR_PESSOA -> resultado.put(responsavelId, valor);
            case PERCENTUAL -> divisoesPercentuais.forEach(divisao -> resultado.put(
                    divisao.pessoaId(),
                    valor.multiply(divisao.percentual()).movePointLeft(2)
            ));
            case VALOR_FIXO_DIVIDIDO -> {
                BigDecimal parcela = valor.divide(BigDecimal.valueOf(pessoasDaCasa.size()));
                pessoasDaCasa.forEach(pessoaId -> resultado.put(pessoaId, parcela));
            }
        }
        return resultado;
    }

    public record DivisaoPercentual(UUID pessoaId, BigDecimal percentual) {
    }
}
