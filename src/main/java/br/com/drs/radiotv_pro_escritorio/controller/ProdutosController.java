package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.ProdutosDTO;
import br.com.drs.radiotv_pro_escritorio.model.Produtos;
import br.com.drs.radiotv_pro_escritorio.service.ProdutosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/produtos")
@RequiredArgsConstructor
public class ProdutosController {

    private final ProdutosService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody ProdutosDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Produto salvo com sucesso!");
    }

    @GetMapping
    public List<Produtos> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Produtos> buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody ProdutosDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Produto atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}
