package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusRecebimento;
import br.com.drs.radiotv_pro_escritorio.service.ContratoPagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contratosPagamento")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContratoPagamentoController {

    private final ContratoPagamentoService service;

    @PostMapping("/gerar/{contratoId}")
    public ResponseEntity<List<ContratoPagamentoDTO>> gerarParcelas(@PathVariable Long contratoId) {
        List<ContratoPagamentoDTO> parcelas = service.gerarParcelasDoContrato(contratoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(parcelas);
    }

    @PostMapping("/regenerar/{contratoId}")
    public ResponseEntity<List<ContratoPagamentoDTO>> regenerarParcelas(@PathVariable Long contratoId) {
        List<ContratoPagamentoDTO> parcelas = service.regenerarParcelas(contratoId);
        return ResponseEntity.ok(parcelas);
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<ContratoPagamentoDTO> atualizarParcela(
            @PathVariable Long id,
            @RequestBody ContratoPagamentoDTO dto) {
        return ResponseEntity.ok(service.atualizarParcela(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirParcela(@PathVariable Long id) {
        service.excluirParcela(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/faturar")
    public ResponseEntity<ContratoPagamentoDTO> faturarParcela(
            @PathVariable Long id,
            @RequestParam String numeroFatura) {
        return ResponseEntity.ok(service.faturarParcela(id, numeroFatura));
    }

    @PostMapping("/{id}/receber")
    public ResponseEntity<ContratoPagamentoDTO> receberPagamento(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> body) {
        BigDecimal valorRecebido = body.get("valorRecebido");
        return ResponseEntity.ok(service.receberPagamento(id, valorRecebido));
    }

    @PostMapping("/processar-atrasos")
    public ResponseEntity<Map<String, Integer>> processarAtrasos() {
        int quantidade = service.marcarParcelasAtrasadas();
        return ResponseEntity.ok(Map.of("parcelasMarcadasComoAtrasadas", quantidade));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarParcela(
            @PathVariable Long id,
            @RequestParam String motivo) {
        service.cancelarParcela(id, motivo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comissoes/vendedor/pendentes")
    public ResponseEntity<List<ContratoPagamentoDTO>> listarComissoesVendedorPendentes() {
        return ResponseEntity.ok(service.listarComissoesVendedorPendentes());
    }

    @GetMapping("/comissoes/agencia/pendentes")
    public ResponseEntity<List<ContratoPagamentoDTO>> listarComissoesAgenciaPendentes() {
        return ResponseEntity.ok(service.listarComissoesAgenciaPendentes());
    }
}