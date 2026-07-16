package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.FolhaPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.model.FolhaPagamento;
import br.com.drs.radiotv_pro_escritorio.service.FolhaPagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/folhaPagamento")
@RequiredArgsConstructor
public class FolhaPagamentoController {

    private final FolhaPagamentoService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody FolhaPagamentoDTO dto){
        service.salvar(dto);
        return ResponseEntity.ok("Folha de pagamento salva com sucesso!");
    }

    @GetMapping
    public List<FolhaPagamento> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<FolhaPagamento> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody FolhaPagamentoDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Folha de apgamento atualizada com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}