package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.FeriadoDTO;
import br.com.drs.radiotv_pro_escritorio.model.Feriado;
import br.com.drs.radiotv_pro_escritorio.service.FeriadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/feriado")
@RequiredArgsConstructor
public class FeriadoController {

    private final FeriadoService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody FeriadoDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Feriado salvo com sucesso!");
    }

    @GetMapping
    public List<Feriado> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Feriado> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody FeriadoDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Feriado atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}