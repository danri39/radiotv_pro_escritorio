package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.ComprasDTO;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusCompra;
import br.com.drs.radiotv_pro_escritorio.service.ComprasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Ajuste conforme sua configuração de CORS
public class ComprasController {

    private final ComprasService comprasService;

    // ==========================================
    // 📝 CRIAÇÃO DE COMPRA (Usuário do Escritório)
    // ==========================================
    @PostMapping
    public ResponseEntity<ComprasDTO> criarCompra(@RequestBody ComprasDTO dto) {
        ComprasDTO compraCriada = comprasService.criarCompra(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compraCriada);
    }

    // ==========================================
    // 📋 LISTAGEM GERAL (Administrador / Gerência)
    // ==========================================
    @GetMapping
    public ResponseEntity<List<ComprasDTO>> listarTodas() {
        List<ComprasDTO> compras = comprasService.listarTodas();
        return ResponseEntity.ok(compras);
    }

    // ==========================================
    // 🔍 BUSCA POR ID (Qualquer usuário autenticado)
    // ==========================================
    @GetMapping("/{id}")
    public ResponseEntity<ComprasDTO> buscarPorId(@PathVariable Long id) {
        ComprasDTO compra = comprasService.buscarPorId(id);
        return ResponseEntity.ok(compra);
    }

    // ==========================================
    // 👤 LISTAR COMPRAS DO FUNCIONÁRIO LOGADO (Escritório)
    // ==========================================
    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<ComprasDTO>> listarPorFuncionario(@PathVariable Long funcionarioId) {
        List<ComprasDTO> compras = comprasService.listarPorFuncionario(funcionarioId);
        return ResponseEntity.ok(compras);
    }

    // ==========================================
    // 📊 LISTAR POR STATUS (Filtros específicos)
    // ==========================================
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ComprasDTO>> listarPorStatus(@PathVariable StatusCompra status) {
        List<ComprasDTO> compras = comprasService.listarPorStatus(status);
        return ResponseEntity.ok(compras);
    }

    // ==========================================
    // 🚨 PAINEL DO ADMINISTRADOR: PENDÊNCIAS DE APROVAÇÃO
    // ==========================================
    @GetMapping("/pendentes")
    public ResponseEntity<List<ComprasDTO>> listarPendentesAprovacao() {
        List<ComprasDTO> compras = comprasService.listarPendentesAprovacao();
        return ResponseEntity.ok(compras);
    }

    // ==========================================
    // ✏️ EDITAR COMPRA (Usuário do Escritório)
    // ==========================================
    @PutMapping("/{id}")
    public ResponseEntity<ComprasDTO> editarCompra(@PathVariable Long id, @RequestBody ComprasDTO dto) {
        ComprasDTO compraEditada = comprasService.editarCompra(id, dto);
        return ResponseEntity.ok(compraEditada);
    }

    // ==========================================
    // ✅ APROVAR COMPRA (Administrador)
    // ==========================================
    @PostMapping("/{id}/aprovar")
    public ResponseEntity<ComprasDTO> aprovarCompra(@PathVariable Long id) {
        ComprasDTO compraAprovada = comprasService.aprovarCompra(id);
        return ResponseEntity.ok(compraAprovada);
    }

    // ==========================================
    // ❌ RECUSAR COMPRA (Administrador)
    // ==========================================
    @PostMapping("/{id}/recusar")
    public ResponseEntity<ComprasDTO> recusarCompra(
            @PathVariable Long id,
            @RequestParam String justificativa) {
        ComprasDTO compraRecusada = comprasService.recusarCompra(id, justificativa);
        return ResponseEntity.ok(compraRecusada);
    }

    // ==========================================
    // 💰 PAGAR / DAR BAIXA (Usuário do Escritório)
    // ==========================================
    @PostMapping("/{id}/pagar")
    public ResponseEntity<ComprasDTO> pagarCompra(@PathVariable Long id) {
        ComprasDTO compraPaga = comprasService.pagarCompra(id);
        return ResponseEntity.ok(compraPaga);
    }

    // ==========================================
    // 🗑️ INATIVAR COMPRA (Administrador)
    // ==========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativarCompra(@PathVariable Long id) {
        comprasService.inativarCompra(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // 🔄 REATIVAR COMPRA (Administrador)
    // ==========================================
    @PostMapping("/{id}/reativar")
    public ResponseEntity<Void> reativarCompra(@PathVariable Long id) {
        comprasService.reativarCompra(id);
        return ResponseEntity.ok().build();
    }
}
