package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.FolhaPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusFolha;
import br.com.drs.radiotv_pro_escritorio.service.FolhaPagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/folhas-pagamento")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FolhaPagamentoController {

    private final FolhaPagamentoService service;

    // ==========================================
    // 📝 FECHAR FOLHA PARA UM FUNCIONÁRIO (RH/Escritório)
    // ==========================================
    /**
     * Fecha a folha de pagamento para um funcionário específico em um mês.
     * O sistema calcula automaticamente:
     * - Salário bruto
     * - Comissões do vendedor (se aplicável)
     * - Benefícios (plano de saúde + coparticipações)
     * - Outros descontos
     * - Salário líquido
     */
    @PostMapping("/fechar/funcionario/{funcionarioId}/mes/{mesReferencia}")
    public ResponseEntity<FolhaPagamentoDTO> fecharFolhaFuncionario(
            @PathVariable Long funcionarioId,
            @PathVariable String mesReferencia) {
        FolhaPagamentoDTO folha = service.fecharFolhaFuncionario(funcionarioId, mesReferencia);
        return ResponseEntity.status(HttpStatus.CREATED).body(folha);
    }

    // ==========================================
    // 📝 FECHAR FOLHA MENSAL EM LOTE (RH/Escritório)
    // ==========================================
    /**
     * Fecha a folha de todos os funcionários ativos em um mês.
     * Usado pelo RH no fechamento mensal.
     */
    @PostMapping("/fechar/mes/{mesReferencia}")
    public ResponseEntity<List<FolhaPagamentoDTO>> fecharFolhaMensal(@PathVariable String mesReferencia) {
        List<FolhaPagamentoDTO> folhas = service.fecharFolhaMensal(mesReferencia);
        return ResponseEntity.status(HttpStatus.CREATED).body(folhas);
    }

    // ==========================================
    // 📋 LISTAGENS GERAIS
    // ==========================================
    @GetMapping
    public ResponseEntity<List<FolhaPagamentoDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FolhaPagamentoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<FolhaPagamentoDTO>> listarPorFuncionario(@PathVariable Long funcionarioId) {
        return ResponseEntity.ok(service.listarPorFuncionario(funcionarioId));
    }

    @GetMapping("/mes/{mesReferencia}")
    public ResponseEntity<List<FolhaPagamentoDTO>> listarPorMesReferencia(@PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.listarPorMesReferencia(mesReferencia));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FolhaPagamentoDTO>> listarPorStatus(@PathVariable StatusFolha status) {
        return ResponseEntity.ok(service.listarPorStatus(status));
    }

    @GetMapping("/fechadas")
    public ResponseEntity<List<FolhaPagamentoDTO>> listarFolhasFechadas() {
        return ResponseEntity.ok(service.listarFolhasFechadas());
    }

    @GetMapping("/pagas/periodo")
    public ResponseEntity<List<FolhaPagamentoDTO>> listarFolhasPagasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.listarFolhasPagasPorPeriodo(inicio, fim));
    }

    // ==========================================
    // ✏️ EDITAR FOLHA (RH/Escritório - Apenas quando ABERTA)
    // ==========================================
    /**
     * Permite editar valores da folha (comissões, benefícios, outros descontos).
     * Só pode ser editada se status = ABERTA.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FolhaPagamentoDTO> editar(
            @PathVariable Long id,
            @RequestBody FolhaPagamentoDTO dto) {
        return ResponseEntity.ok(service.editar(id, dto));
    }

    // ==========================================
    // 💰 PAGAR FOLHA (Escritório)
    // ==========================================
    /**
     * Marca a folha como PAGA e gera um lançamento no módulo de Pagamentos.
     */
    @PostMapping("/{id}/pagar")
    public ResponseEntity<FolhaPagamentoDTO> pagarFolha(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String formaPagamento = body.get("formaPagamento");
        return ResponseEntity.ok(service.pagarFolha(id, formaPagamento));
    }

    // ==========================================
    // ❌ CANCELAR FOLHA (Administrador)
    // ==========================================
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @RequestParam String motivo) {
        service.cancelar(id, motivo);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // 📊 RELATÓRIOS FINANCEIROS (Administrativo/Gerencial)
    // ==========================================

    /**
     * Soma o total de salários líquidos pagos em um mês
     */
    @GetMapping("/relatorios/total-liquido/mes/{mesReferencia}")
    public ResponseEntity<BigDecimal> somarTotalLiquidoPagoPorMes(@PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.somarTotalLiquidoPagoPorMes(mesReferencia));
    }

    /**
     * Soma o total de comissões pagas em um mês
     */
    @GetMapping("/relatorios/total-comissoes/mes/{mesReferencia}")
    public ResponseEntity<BigDecimal> somarTotalComissoesPagasPorMes(@PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.somarTotalComissoesPagasPorMes(mesReferencia));
    }

    /**
     * Soma o total de benefícios descontados em um mês
     */
    @GetMapping("/relatorios/total-beneficios/mes/{mesReferencia}")
    public ResponseEntity<BigDecimal> somarTotalBeneficiosDescontadosPorMes(@PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.somarTotalBeneficiosDescontadosPorMes(mesReferencia));
    }

    /**
     * Lista folhas de vendedores em um mês (para relatório de comissões)
     */
    @GetMapping("/relatorios/vendedores/mes/{mesReferencia}")
    public ResponseEntity<List<FolhaPagamentoDTO>> listarFolhasVendedoresPorMes(@PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.listarFolhasVendedoresPorMes(mesReferencia));
    }

    // ==========================================
    // 📈 DASHBOARD / CONTAGENS
    // ==========================================
    @GetMapping("/dashboard/status/{status}")
    public ResponseEntity<Long> contarFolhasPorStatus(@PathVariable StatusFolha status) {
        return ResponseEntity.ok(service.contarFolhasPorStatus(status));
    }

    @GetMapping("/dashboard/mes/{mesReferencia}")
    public ResponseEntity<Long> contarFolhasPorMes(@PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.contarFolhasPorMes(mesReferencia));
    }
}