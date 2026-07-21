package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.GerencialComercialDTO;
import br.com.drs.radiotv_pro_escritorio.dto.GerencialFinanceiroDTO;
import br.com.drs.radiotv_pro_escritorio.dto.GerencialRHDTO;
import br.com.drs.radiotv_pro_escritorio.service.GerencialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/gerencial")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GerencialController {

    private final GerencialService service;

    /**
     * Visão do Gerente Comercial
     * Contratos, vendedores, agências, metas, ranking
     */
    @GetMapping("/comercial")
    public ResponseEntity<GerencialComercialDTO> getVisaoComercial(
            @RequestParam(required = false) String mes) {
        return ResponseEntity.ok(service.gerarVisaoComercial(mes));
    }

    /**
     * Visão do Gerente Financeiro
     * Receitas, despesas, fluxo de caixa, contas a pagar, bancos
     */
    @GetMapping("/financeiro")
    public ResponseEntity<GerencialFinanceiroDTO> getVisaoFinanceira(
            @RequestParam(required = false) String mes) {
        return ResponseEntity.ok(service.gerarVisaoFinanceira(mes));
    }

    /**
     * Visão do Gerente de RH
     * Funcionários, folhas, benefícios, comissões
     */
    @GetMapping("/rh")
    public ResponseEntity<GerencialRHDTO> getVisaoRH(
            @RequestParam(required = false) String mes) {
        return ResponseEntity.ok(service.gerarVisaoRH(mes));
    }
}