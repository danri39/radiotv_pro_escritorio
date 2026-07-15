package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.ComprasDTO;
import br.com.drs.radiotv_pro_escritorio.model.Compras;
import br.com.drs.radiotv_pro_escritorio.service.ComprasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/compras")
@RequiredArgsConstructor
public class ComprasController {

    private final ComprasService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody ComprasDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Compras salva com sucesso!");
    }

    @GetMapping
    public List<Compras> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Compras> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody ComprasDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Compra Atualizada com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}
