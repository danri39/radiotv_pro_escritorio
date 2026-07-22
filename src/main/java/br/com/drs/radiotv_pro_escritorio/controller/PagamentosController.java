package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.PagamentosDTO;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusPagamento;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPagamento;
import br.com.drs.radiotv_pro_escritorio.service.PagamentosService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pagamentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PagamentosController {

    private final PagamentosService service;

    // ==========================================
    // 💵 CRIAR CONTA DIVERSA (Escritório)
    // ==========================================
    @PostMapping("/conta-diversa")
    public ResponseEntity<PagamentosDTO> criarContaDiversa(@RequestBody PagamentosDTO dto) {
        PagamentosDTO criado = service.criarContaDiversa(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    // ==========================================
    // 📋 LISTAGENS GERAIS
    // ==========================================
    @GetMapping
    public ResponseEntity<List<PagamentosDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentosDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PagamentosDTO>> listarPorStatus(@PathVariable StatusPagamento status) {
        return ResponseEntity.ok(service.listarPorStatus(status));
    }

    @GetMapping("/prontos")
    public ResponseEntity<List<PagamentosDTO>> listarProntosParaPagamento() {
        return ResponseEntity.ok(service.listarProntosParaPagamento());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<PagamentosDTO>> listarPorTipo(@PathVariable TipoPagamento tipo) {
        return ResponseEntity.ok(service.listarPorTipo(tipo));
    }

    @GetMapping("/agencia/{agenciaId}")
    public ResponseEntity<List<PagamentosDTO>> listarPorAgencia(@PathVariable Long agenciaId) {
        return ResponseEntity.ok(service.listarPorAgencia(agenciaId));
    }

    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<PagamentosDTO>> listarPorFuncionario(@PathVariable Long funcionarioId) {
        return ResponseEntity.ok(service.listarPorFuncionario(funcionarioId));
    }

    @GetMapping("/periodo-vencimento")
    public ResponseEntity<List<PagamentosDTO>> listarPorPeriodoVencimento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.listarPorPeriodoVencimento(inicio, fim));
    }

    @GetMapping("/periodo-pagamento")
    public ResponseEntity<List<PagamentosDTO>> listarPorPeriodoPagamento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.listarPorPeriodoPagamento(inicio, fim));
    }

    @GetMapping("/vencidos")
    public ResponseEntity<List<PagamentosDTO>> listarVencidos() {
        return ResponseEntity.ok(service.listarVencidos());
    }

    // ==========================================
    // 🏢 PORTAL DA AGÊNCIA: Comissões pendentes de documento
    // ==========================================
    @GetMapping("/agencia/{agenciaId}/pendentes-documento")
    public ResponseEntity<List<PagamentosDTO>> listarComissoesAgenciaPendentesDocumento(
            @PathVariable Long agenciaId) {
        return ResponseEntity.ok(service.listarComissoesAgenciaAguardandoDocumento(agenciaId));
    }

    // ==========================================
    // 📤 PORTAL DA AGÊNCIA: Upload do Documento da NF
    // ==========================================
    /**
     * A agência faz upload da NF/Boleto.
     * Usa multipart/form-data para enviar o arquivo + número do documento.
     *
     * Exemplo de chamada (curl):
     * curl -X POST "http://localhost:8080/api/pagamentos/5/documento-agencia?agenciaId=2&numeroDocumento=NF-12345" \
     *      -F "arquivo=@/caminho/para/nota.pdf"
     */
    @PostMapping("/{id}/documento-agencia")
    public ResponseEntity<PagamentosDTO> registrarDocumentoAgencia(
            @PathVariable Long id,
            @RequestParam Long agenciaId,
            @RequestParam String numeroDocumento,
            @RequestParam("arquivo") MultipartFile arquivo) throws IOException {

        PagamentosDTO atualizado = service.registrarDocumentoAgencia(id, agenciaId, numeroDocumento, arquivo);
        return ResponseEntity.ok(atualizado);
    }

    // ==========================================
    // 💰 PAGAR / DAR BAIXA (Escritório)
    // ==========================================
    @PostMapping("/{id}/pagar")
    public ResponseEntity<PagamentosDTO> pagar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String formaPagamento = body.get("formaPagamento");
        return ResponseEntity.ok(service.pagar(id, formaPagamento));
    }

    // ==========================================
    // ❌ CANCELAR PAGAMENTO (Administrador)
    // ==========================================
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @RequestParam String motivo) {
        service.cancelar(id, motivo);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // ✏️ EDITAR CONTA DIVERSA (Escritório)
    // ==========================================
    @PutMapping("/{id}")
    public ResponseEntity<PagamentosDTO> editar(
            @PathVariable Long id,
            @RequestBody PagamentosDTO dto) {
        return ResponseEntity.ok(service.editar(id, dto));
    }

    // ==========================================
    // 📊 PAINEL ADMIN: Pagamentos em atraso
    // ==========================================
    @GetMapping("/atraso")
    public ResponseEntity<List<PagamentosDTO>> listarEmAtraso() {
        return ResponseEntity.ok(service.listarPagamentosEmAtraso());
    }
}