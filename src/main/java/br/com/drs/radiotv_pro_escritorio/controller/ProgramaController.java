package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.ProgramaDTO;
import br.com.drs.radiotv_pro_escritorio.model.Programa;
import br.com.drs.radiotv_pro_escritorio.service.ProgramaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/programas")
@RequiredArgsConstructor
public class ProgramaController {

    private final ProgramaService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody ProgramaDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Programa salvo com sucesso!");
    }

    @GetMapping
    public List<Programa> ListarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Programa> BuscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody ProgramaDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Programa salvo com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}
