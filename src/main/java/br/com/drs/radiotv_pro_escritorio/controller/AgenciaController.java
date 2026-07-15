package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.AgenciaDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.AgenciaMapper;
import br.com.drs.radiotv_pro_escritorio.model.Agencia;
import br.com.drs.radiotv_pro_escritorio.service.AgenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/agencias")
@RequiredArgsConstructor
public class AgenciaController {

    private final AgenciaService service;
    private final AgenciaMapper mapper;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody AgenciaDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Agência salva com sucesso!");
    }

    @GetMapping
    public List<Agencia> listarTodos() {
        return service.listaTodos();
    }

    @GetMapping("/{id}")
    public Optional<Agencia> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody AgenciaDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Agência atualizada com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}
