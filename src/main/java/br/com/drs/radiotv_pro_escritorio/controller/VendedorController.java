package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.VendedorDTO;
import br.com.drs.radiotv_pro_escritorio.service.VendedorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/vendedor")
@RequiredArgsConstructor
public class VendedorController {

    private final VendedorService service;

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody VendedorDTO dto) {
        log.info(">>> Controller recebeu: {}", dto);
        try {
            service.salvar(dto);
            return ResponseEntity.ok("Dados salvos com sucesso!");
        } catch (Exception e) {
            log.error("Erro ao salvar vendedor", e);
            return ResponseEntity.badRequest().body(java.util.Map.of("mensagem", e.getMessage()));
        }
    }

    @GetMapping
    public List<?> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<?> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody VendedorDTO dto) {
        try {
            service.atualizar(id, dto);
            return ResponseEntity.ok("Dados atualizados com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("mensagem", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}