package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.FeriasDTO;
import br.com.drs.radiotv_pro_escritorio.model.Ferias;
import br.com.drs.radiotv_pro_escritorio.service.FeriasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/ferias")
@RequiredArgsConstructor
public class FeriasController {

    private final FeriasService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody FeriasDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Férias salva com sucesso!");
    }

    @GetMapping
    public List<Ferias> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Ferias> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody FeriasDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Férias atualziadas copm sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}