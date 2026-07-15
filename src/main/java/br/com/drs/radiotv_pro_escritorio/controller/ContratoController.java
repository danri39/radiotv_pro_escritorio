package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoDTO;
import br.com.drs.radiotv_pro_escritorio.model.Contrato;
import br.com.drs.radiotv_pro_escritorio.service.ContratoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/contratos")
@RequiredArgsConstructor
public class ContratoController {

    private final ContratoService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody ContratoDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Contrato salvo com sucesso!");
    }

    @GetMapping
    public List<Contrato> listarTodos(){
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Contrato> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody ContratoDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Contrato atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}
