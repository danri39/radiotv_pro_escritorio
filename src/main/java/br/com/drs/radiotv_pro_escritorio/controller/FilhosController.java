package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.FilhosDTO;
import br.com.drs.radiotv_pro_escritorio.model.Filhos;
import br.com.drs.radiotv_pro_escritorio.service.FilhosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/filhos")
@RequiredArgsConstructor
public class FilhosController {

    private final FilhosService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody FilhosDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Filho salvo com sucesso!");
    }

    @GetMapping
    public List<FilhosDTO> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public FilhosDTO buscarPOrId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody FilhosDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Filho atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}