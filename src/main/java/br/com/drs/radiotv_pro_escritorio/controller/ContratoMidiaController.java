package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoMidiaDTO;
import br.com.drs.radiotv_pro_escritorio.model.ContratoMidia;
import br.com.drs.radiotv_pro_escritorio.service.ContratoMidiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/contratoMidia")
@RequiredArgsConstructor
public class ContratoMidiaController {

    private final ContratoMidiaService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody ContratoMidiaDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Contrato mídia salvo com sucesso!");
    }

    @GetMapping
    public List<ContratoMidia> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<ContratoMidia> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody ContratoMidiaDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Contrato mídia atualizado com sucesso! ");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}