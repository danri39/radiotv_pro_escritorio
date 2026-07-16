package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.model.ContratoPagamento;
import br.com.drs.radiotv_pro_escritorio.service.ContratoPagamentoService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/contratoPagamento")
@RequiredArgsConstructor
public class ContratoPagamentoController {

    private final ContratoPagamentoService service;

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody ContratoPagamentoDTO dto) {
        service.salvar(dto);
        return ResponseEntity.ok("Pagamento salvo com sucesso!");
    }

    @GetMapping
    public List<ContratoPagamento> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<ContratoPagamento> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody ContratoPagamentoDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.ok("Contrato bancario atualizado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}