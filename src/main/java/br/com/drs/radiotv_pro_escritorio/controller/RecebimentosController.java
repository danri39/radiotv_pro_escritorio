package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.GerarParcelasRequestDTO;
import br.com.drs.radiotv_pro_escritorio.dto.RecebimentosResponseDTO;
import br.com.drs.radiotv_pro_escritorio.service.RecebimentosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/recebimentos")
public class RecebimentosController {

    private final RecebimentosService recebimentosService;

    public RecebimentosController(RecebimentosService recebimentosService) {
        this.recebimentosService = recebimentosService;
    }

    @PostMapping("/gerar-parcelas")
    public ResponseEntity<List<RecebimentosResponseDTO>> gerarParcelas(
            @RequestBody GerarParcelasRequestDTO dto) {
        return ResponseEntity.ok(recebimentosService.gerarParcelas(dto));
    }

    @GetMapping("/contrato/{contratoId}")
    public ResponseEntity<List<RecebimentosResponseDTO>> listarPorContrato(
            @PathVariable Long contratoId) {
        return ResponseEntity.ok(recebimentosService.listarPorContrato(contratoId));
    }

    @PatchMapping("/{id}/baixa")
    public ResponseEntity<RecebimentosResponseDTO> darBaixa(@PathVariable Long id) {
        return ResponseEntity.ok(recebimentosService.darBaixa(id));
    }

    @PostMapping("/vendedor/{vendedorId}/fechar-comissoes")
    public ResponseEntity<Map<String, Object>> fecharComissoes(@PathVariable Long vendedorId) {
        BigDecimal total = recebimentosService.fecharComissoesVendedor(vendedorId);

        return ResponseEntity.ok(Map.of(
                "vendedorId", vendedorId,
                "totalComissoes", total,
                "mensagem", "Comissões fechadas e marcadas como pagas na folha/vale"
        ));
    }
}