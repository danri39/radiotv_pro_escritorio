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
    public List<Filhos> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{filhosId}")
    public ResponseEntity<Filhos> buscarPorId(@PathVariable Long filhosId) {
        return service.buscarPorId(filhosId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{filhosId}")
    public ResponseEntity<String> atualizar(@PathVariable Long filhosId, @RequestBody FilhosDTO dto) {
        service.atualizar(filhosId, dto);
        return ResponseEntity.ok("Filho atualizado com sucesso!");
    }

    @DeleteMapping("/{filhosId}")
    public ResponseEntity<String> apagar(@PathVariable Long filhosId) {
        service.apagar(filhosId);
        return ResponseEntity.ok("Filho excluído com sucesso!");
    }
}