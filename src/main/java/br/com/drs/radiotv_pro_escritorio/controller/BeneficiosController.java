package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.BeneficiosDTO;
import br.com.drs.radiotv_pro_escritorio.model.Beneficios;
import br.com.drs.radiotv_pro_escritorio.service.BeneficiosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/beneficios")
@RequiredArgsConstructor
public class BeneficiosController {

    private final BeneficiosService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody BeneficiosDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Beneficio salvo com sucesso!");
    }

    @GetMapping
    public List<Beneficios> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Beneficios> buscarPorId(@PathVariable Long id) {
        return service.buscarProId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody BeneficiosDTO dto) {
        return ResponseEntity.ok("Beneficio atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}
