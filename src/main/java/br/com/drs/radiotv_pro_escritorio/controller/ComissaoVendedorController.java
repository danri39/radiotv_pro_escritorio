package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.ComissaoVendedorDTO;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusComissao;
import br.com.drs.radiotv_pro_escritorio.service.ComissaoVendedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/comissoes-vendedor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComissaoVendedorController {

    private final ComissaoVendedorService service;

    // ==========================================
    // 📋 LISTAGENS GERAIS
    // ==========================================
    @GetMapping
    public ResponseEntity<List<ComissaoVendedorDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComissaoVendedorDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<List<ComissaoVendedorDTO>> listarPorVendedor(@PathVariable Long vendedorId) {
        return ResponseEntity.ok(service.listarPorVendedor(vendedorId));
    }

    @GetMapping("/vendedor/{vendedorId}/status/{status}")
    public ResponseEntity<List<ComissaoVendedorDTO>> listarPorVendedorEStatus(
            @PathVariable Long vendedorId,
            @PathVariable StatusComissao status) {
        return ResponseEntity.ok(service.listarPorVendedorEStatus(vendedorId, status));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ComissaoVendedorDTO>> listarPorStatus(@PathVariable StatusComissao status) {
        return ResponseEntity.ok(service.listarPorStatus(status));
    }

    @GetMapping("/contrato/{contratoId}")
    public ResponseEntity<List<ComissaoVendedorDTO>> listarPorContrato(@PathVariable Long contratoId) {
        return ResponseEntity.ok(service.listarPorContrato(contratoId));
    }

    @GetMapping("/periodo-calculo")
    public ResponseEntity<List<ComissaoVendedorDTO>> listarPorPeriodoCalculo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.listarPorPeriodoCalculo(inicio, fim));
    }

    @GetMapping("/periodo-processamento")
    public ResponseEntity<List<ComissaoVendedorDTO>> listarPorPeriodoProcessamento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.listarPorPeriodoProcessamento(inicio, fim));
    }

    // ==========================================
    // 💼 LISTAGENS PARA FOLHA DE PAGAMENTO
    // ==========================================
    @GetMapping("/pendentes/vendedor/{vendedorId}/mes/{mesReferencia}")
    public ResponseEntity<List<ComissaoVendedorDTO>> listarPendentesPorVendedorEMes(
            @PathVariable Long vendedorId,
            @PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.listarPendentesPorVendedorEMes(vendedorId, mesReferencia));
    }

    @GetMapping("/pendentes/mes/{mesReferencia}")
    public ResponseEntity<List<ComissaoVendedorDTO>> listarPendentesPorMes(@PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.listarPendentesPorMes(mesReferencia));
    }

    @GetMapping("/total-pendente/vendedor/{vendedorId}/mes/{mesReferencia}")
    public ResponseEntity<BigDecimal> somarComissoesPendentes(
            @PathVariable Long vendedorId,
            @PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.somarComissoesPendentes(vendedorId, mesReferencia));
    }

    // ==========================================
    // ⚙️ PROCESSAR EM LOTE (Chamado pelo sistema ao fechar folha)
    // ==========================================
    /**
     * Processa todas as comissões pendentes de um vendedor em um mês.
     * Este endpoint é chamado internamente pelo FolhaPagamentoService.
     *
     * Exemplo de body:
     * {
     *   "folhaPagamentoId": 123
     * }
     */
    @PostMapping("/processar/vendedor/{vendedorId}/mes/{mesReferencia}")
    public ResponseEntity<List<ComissaoVendedorDTO>> processarEmLote(
            @PathVariable Long vendedorId,
            @PathVariable String mesReferencia,
            @RequestBody Map<String, Long> body) {
        Long folhaPagamentoId = body.get("folhaPagamentoId");
        return ResponseEntity.ok(service.processarEmLote(vendedorId, mesReferencia, folhaPagamentoId));
    }

    // ==========================================
    // ❌ CANCELAR COMISSÃO (Administrador)
    // ==========================================
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @RequestParam String motivo) {
        service.cancelar(id, motivo);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // 📊 DASHBOARD / CONTAGENS
    // ==========================================
    @GetMapping("/dashboard/pendentes/vendedor/{vendedorId}")
    public ResponseEntity<Long> contarPendentesPorVendedor(@PathVariable Long vendedorId) {
        return ResponseEntity.ok(service.contarPendentesPorVendedor(vendedorId));
    }

    @GetMapping("/dashboard/pendentes/total")
    public ResponseEntity<Long> contarPendentesTotal() {
        return ResponseEntity.ok(service.contarPendentesTotal());
    }

    @GetMapping("/dashboard/processadas/total")
    public ResponseEntity<Long> contarProcessadasTotal() {
        return ResponseEntity.ok(service.contarProcessadasTotal());
    }
}