package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.RamoAtividadeDTO;
import br.com.drs.radiotv_pro_escritorio.model.RamoAtividade;
import br.com.drs.radiotv_pro_escritorio.service.RamoAtividadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/ramoAtividade")
@RequiredArgsConstructor
public class RamoAtividadeController {

    private final RamoAtividadeService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody RamoAtividadeDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Ramo atividade salvo com sucesso!");
    }

    @GetMapping
    public List<RamoAtividade> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<RamoAtividade> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody RamoAtividadeDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Ramo de atividade atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}
