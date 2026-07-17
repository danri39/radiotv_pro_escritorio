package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.PontoDTO;
import br.com.drs.radiotv_pro_escritorio.model.Ponto;
import br.com.drs.radiotv_pro_escritorio.service.PontoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/ponto")
@RequiredArgsConstructor
public class PontoController {

    private final PontoService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody PontoDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Ponto salvo com sucesso!");
    }

    @GetMapping
    public List<Ponto> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Ponto> buscarTodos(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody PontoDTO dto){
        service.atualizar(id, dto);
        return ResponseEntity.ok("Ponto atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}