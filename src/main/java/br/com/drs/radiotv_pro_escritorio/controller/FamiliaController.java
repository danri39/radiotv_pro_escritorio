package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.FamiliaDTO;
import br.com.drs.radiotv_pro_escritorio.model.Familia;
import br.com.drs.radiotv_pro_escritorio.service.FamiliaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/familias")
@RequiredArgsConstructor
public class FamiliaController {

    private final FamiliaService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody FamiliaDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Familia salva com sucesso!");
    }

    @GetMapping
    public List<Familia> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Familia> buscarPorId(@PathVariable Long id) {
        return service.BuscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody FamiliaDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Família atualizada com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}
