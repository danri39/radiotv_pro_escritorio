package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.PlanoBeneficioDTO;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoBeneficio;
import br.com.drs.radiotv_pro_escritorio.service.PlanoBeneficioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planos-beneficio")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlanoBeneficioController {

    private final PlanoBeneficioService service;

    // ==========================================
    // 📝 CRIAR PLANO DE BENEFÍCIO (Administrador/RH)
    // ==========================================
    @PostMapping
    public ResponseEntity<PlanoBeneficioDTO> criar(@RequestBody PlanoBeneficioDTO dto) {
        PlanoBeneficioDTO criado = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    // ==========================================
    // 📋 LISTAGENS
    // ==========================================
    @GetMapping
    public ResponseEntity<List<PlanoBeneficioDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanoBeneficioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<PlanoBeneficioDTO>> listarPorTipo(@PathVariable TipoBeneficio tipo) {
        return ResponseEntity.ok(service.listarPorTipo(tipo));
    }

    @GetMapping("/operadora/{operadora}")
    public ResponseEntity<List<PlanoBeneficioDTO>> listarPorOperadora(@PathVariable String operadora) {
        return ResponseEntity.ok(service.listarPorOperadora(operadora));
    }

    // ==========================================
    // ✏️ EDITAR PLANO DE BENEFÍCIO (Administrador/RH)
    // ==========================================
    @PutMapping("/{id}")
    public ResponseEntity<PlanoBeneficioDTO> editar(
            @PathVariable Long id,
            @RequestBody PlanoBeneficioDTO dto) {
        return ResponseEntity.ok(service.editar(id, dto));
    }

    // ==========================================
    // 🗑️ INATIVAR PLANO (Administrador/RH)
    // ==========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // 🔄 REATIVAR PLANO (Administrador/RH)
    // ==========================================
    @PostMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(@PathVariable Long id) {
        service.reativar(id);
        return ResponseEntity.ok().build();
    }
}