package com.financa.relatorio;

import com.financa.relatorio.dto.RelatorioMensalResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
@Validated
public class RelatorioController {

    private final RelatorioMensalService relatorioMensalService;

    @GetMapping("/mensal")
    public RelatorioMensalResponse mensal(
            @RequestParam("mes") @NotBlank @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])$") String mes
    ) {
        return relatorioMensalService.gerar(mes);
    }
}
