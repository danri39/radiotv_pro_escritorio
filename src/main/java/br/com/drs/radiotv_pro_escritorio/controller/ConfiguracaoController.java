package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.ConfiguracaoDTO;
import br.com.drs.radiotv_pro_escritorio.model.Configuracao;
import br.com.drs.radiotv_pro_escritorio.service.ConfiguracaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/configuracao")
@RequiredArgsConstructor
public class ConfiguracaoController {

    private final ConfiguracaoService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody ConfiguracaoDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Configuração salva com sucesso!");
    }

    @GetMapping
    public List<Configuracao> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Configuracao> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody ConfiguracaoDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Configuração atualizada com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}
