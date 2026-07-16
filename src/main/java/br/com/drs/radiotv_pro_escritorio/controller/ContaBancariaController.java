package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.ContaBancariaDTO;
import br.com.drs.radiotv_pro_escritorio.model.ContaBancaria;
import br.com.drs.radiotv_pro_escritorio.service.ContaBancariaService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/contaBancaria")
@RequiredArgsConstructor
public class ContaBancariaController {

    private final ContaBancariaService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody ContaBancariaDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Conta bancaria salva com sucesso!");
    }

    @GetMapping
    public List<ContaBancaria> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<ContaBancaria> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody ContaBancariaDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Conta bancaria atualizada com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}