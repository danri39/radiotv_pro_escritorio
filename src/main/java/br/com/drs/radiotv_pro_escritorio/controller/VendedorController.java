package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.VendedorDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.VendedorMapper;
import br.com.drs.radiotv_pro_escritorio.model.Vendedor;
import br.com.drs.radiotv_pro_escritorio.service.VendedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/vendedor")
@RequiredArgsConstructor
public class VendedorController {

    private final VendedorService service;
    private final VendedorMapper mapper;

    @PostMapping
    public ResponseEntity<String> salvar(VendedorDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Dados salvo com sucesso!");
    }

    @GetMapping
    public List<Vendedor> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Vendedor> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody VendedorDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Dados salvos comk sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}
