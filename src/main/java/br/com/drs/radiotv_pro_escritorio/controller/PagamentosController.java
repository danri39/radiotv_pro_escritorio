package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.PagamentosDTO;
import br.com.drs.radiotv_pro_escritorio.dto.PagamentosRequestDTO;
import br.com.drs.radiotv_pro_escritorio.service.PagamentosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentosController {

    private final PagamentosService pagamentosService;

    @PostMapping("/manual")
    public ResponseEntity<PagamentosDTO> lancarManual(@RequestBody PagamentosRequestDTO dto) {
        return ResponseEntity.ok(pagamentosService.criarPagamentoManual(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentosDTO> atualizar(@PathVariable Long id,
                                                   @RequestBody PagamentosRequestDTO dto) {
        return ResponseEntity.ok(pagamentosService.atualizarPagamento(id, dto));
    }

    @PatchMapping("/{id}/baixa")
    public ResponseEntity<PagamentosDTO> darBaixa(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentosService.darBaixa(id));
    }

    @GetMapping
    public ResponseEntity<List<PagamentosDTO>> listarTodos() {
        return ResponseEntity.ok(pagamentosService.listarTodos());
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<PagamentosDTO>> listarPendentes() {
        return ResponseEntity.ok(pagamentosService.listarPendentes());
    }
}