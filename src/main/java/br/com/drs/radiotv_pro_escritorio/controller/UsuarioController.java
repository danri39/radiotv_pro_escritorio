package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.UsuarioDTO;
import br.com.drs.radiotv_pro_escritorio.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody UsuarioDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Usuário: " + dto.getPrimeiroNome() +  " salvo com sucesso!");
    }

    @GetMapping
    public ResponseEntity<UsuarioDTO> buscarTodos() {
        return ResponseEntity.ok((UsuarioDTO) service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Usuário: " +dto.getPrimeiroNome()+ " Atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDTO> apagar(@PathVariable Long id) {
        return (ResponseEntity<UsuarioDTO>) ResponseEntity.status(HttpStatus.NO_CONTENT);
    }
}