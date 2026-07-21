package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusRecebimento;
import br.com.drs.radiotv_pro_escritorio.service.ContratoPagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contratos-pagamento")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContratoPagamentoController {

    private final ContratoPagamentoService service;

    // ==========================================
    // 🔄 GERAR PARCELAS DE UM CONTRATO (Automático ao criar contrato)
    // ==========================================
    @PostMapping("/gerar/{contratoId}")
    public ResponseEntity<List<ContratoPagamentoDTO>> gerarParcelas(@PathVariable Long contratoId) {
        List<ContratoPagamentoDTO> parcelas = service.gerarParcelasDoContrato(contratoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(parcelas);
    }

    // ==========================================
    // 📋 LISTAGENS GERAIS
    // ==========================================
    @GetMapping
    public ResponseEntity<List<ContratoPagamentoDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoPagamentoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/contrato/{contratoId}")
    public ResponseEntity<List<ContratoPagamentoDTO>> listarPorContrato(@PathVariable Long contratoId) {
        return ResponseEntity.ok(service.listarPorContrato(contratoId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ContratoPagamentoDTO>> listarPorStatus(@PathVariable StatusRecebimento status) {
        return ResponseEntity.ok(service.listarPorStatus(status));
    }

    @GetMapping("/periodo-recebimento")
    public ResponseEntity<List<ContratoPagamentoDTO>> listarPorPeriodoRecebimento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.listarPorPeriodoRecebimento(inicio, fim));
    }

    @GetMapping("/periodo-vencimento")
    public ResponseEntity<List<ContratoPagamentoDTO>> listarPorPeriodoVencimento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.listarPorPeriodoVencimento(inicio, fim));
    }

    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<List<ContratoPagamentoDTO>> listarPorVendedor(@PathVariable Long vendedorId) {
        return ResponseEntity.ok(service.listarPorVendedor(vendedorId));
    }

    @GetMapping("/agencia/{agenciaId}")
    public ResponseEntity<List<ContratoPagamentoDTO>> listarPorAgencia(@PathVariable Long agenciaId) {
        return ResponseEntity.ok(service.listarPorAgencia(agenciaId));
    }

    // ==========================================
    // 📄 FATURAR PARCELA (Escritório emite boleto)
    // ==========================================
    @PostMapping("/{id}/faturar")
    public ResponseEntity<ContratoPagamentoDTO> faturarParcela(
            @PathVariable Long id,
            @RequestParam String numeroFatura) {
        return ResponseEntity.ok(service.faturarParcela(id, numeroFatura));
    }

    // ==========================================
    // 💰 RECEBER PAGAMENTO / DAR BAIXA (Escritório)
    // ==========================================
    /**
     * Endpoint principal do fluxo financeiro.
     * Ao chamar este endpoint, o sistema automaticamente:
     * - Marca a parcela como RECEBIDA
     * - Lança a comissão do vendedor (ComissaoVendedor)
     * - Lança a comissão da agência (Pagamento)
     */
    @PostMapping("/{id}/receber")
    public ResponseEntity<ContratoPagamentoDTO> receberPagamento(
            @PathVariable Long id,
            @RequestBody Map<String, java.math.BigDecimal> body) {
        java.math.BigDecimal valorRecebido = body.get("valorRecebido");
        return ResponseEntity.ok(service.receberPagamento(id, valorRecebido));
    }

    // ==========================================
    // ⏰ JOB: Marcar parcelas atrasadas (Administrador/Sistema)
    // ==========================================
    @PostMapping("/processar-atrasos")
    public ResponseEntity<Map<String, Integer>> processarAtrasos() {
        int quantidade = service.marcarParcelasAtrasadas();
        return ResponseEntity.ok(Map.of("parcelasMarcadasComoAtrasadas", quantidade));
    }

    // ==========================================
    // ❌ CANCELAR PARCELA (Administrador)
    // ==========================================
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarParcela(
            @PathVariable Long id,
            @RequestParam String motivo) {
        service.cancelarParcela(id, motivo);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // 📊 PAINEL ADMIN: Pendências de comissão
    // ==========================================
    @GetMapping("/comissoes/vendedor/pendentes")
    public ResponseEntity<List<ContratoPagamentoDTO>> listarComissoesVendedorPendentes() {
        return ResponseEntity.ok(service.listarComissoesVendedorPendentes());
    }

    @GetMapping("/comissoes/agencia/pendentes")
    public ResponseEntity<List<ContratoPagamentoDTO>> listarComissoesAgenciaPendentes() {
        return ResponseEntity.ok(service.listarComissoesAgenciaPendentes());
    }
}