package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.ClienteDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.ClienteMapper;
import br.com.drs.radiotv_pro_escritorio.model.Cliente;
import br.com.drs.radiotv_pro_escritorio.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;
    private final ClienteMapper mapper;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody ClienteDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Cliente salvo com sucesso!");
    }

    @GetMapping
    public List<Cliente> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Cliente> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody ClienteDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Cliente Atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}
