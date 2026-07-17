package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.HorariosBreaksDTO;
import br.com.drs.radiotv_pro_escritorio.model.HorariosBreaks;
import br.com.drs.radiotv_pro_escritorio.service.HorariosBreaksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/horariosBreaks")
@RequiredArgsConstructor
public class HorariosBreaksController {

    private final HorariosBreaksService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody HorariosBreaksDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Horários salvos com sucesso!");
    }

    @GetMapping
    public List<HorariosBreaks> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<HorariosBreaks> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, HorariosBreaksDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Horários atualizados com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}