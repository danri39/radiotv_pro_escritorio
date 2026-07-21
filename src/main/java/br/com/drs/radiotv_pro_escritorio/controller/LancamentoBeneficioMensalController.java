package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.dto.LancamentoBeneficioMensalDTO;
import br.com.drs.radiotv_pro_escritorio.service.LancamentoBeneficioMensalService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/lancamentos-beneficio")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LancamentoBeneficioMensalController {

    private final LancamentoBeneficioMensalService service;

    // ==========================================
    // 📝 CRIAR LANÇAMENTO MENSAL (RH/Escritório)
    // ==========================================
    @PostMapping
    public ResponseEntity<LancamentoBeneficioMensalDTO> criar(@RequestBody LancamentoBeneficioMensalDTO dto) {
        LancamentoBeneficioMensalDTO criado = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    // ==========================================
    // 📋 LISTAGENS GERAIS
    // ==========================================
    @GetMapping
    public ResponseEntity<List<LancamentoBeneficioMensalDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LancamentoBeneficioMensalDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<LancamentoBeneficioMensalDTO>> listarPorFuncionario(@PathVariable Long funcionarioId) {
        return ResponseEntity.ok(service.listarPorFuncionario(funcionarioId));
    }

    @GetMapping("/funcionario/{funcionarioId}/mes/{mesReferencia}")
    public ResponseEntity<List<LancamentoBeneficioMensalDTO>> listarPorFuncionarioEMes(
            @PathVariable Long funcionarioId,
            @PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.listarPorFuncionarioEMes(funcionarioId, mesReferencia));
    }

    @GetMapping("/mes/{mesReferencia}")
    public ResponseEntity<List<LancamentoBeneficioMensalDTO>> listarPorMesReferencia(@PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.listarPorMesReferencia(mesReferencia));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<LancamentoBeneficioMensalDTO>> listarPorPeriodoLancamento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.listarPorPeriodoLancamento(inicio, fim));
    }

    // ==========================================
    // ✏️ EDITAR LANÇAMENTO (RH/Escritório)
    // ==========================================
    @PutMapping("/{id}")
    public ResponseEntity<LancamentoBeneficioMensalDTO> editar(
            @PathVariable Long id,
            @RequestBody LancamentoBeneficioMensalDTO dto) {
        return ResponseEntity.ok(service.editar(id, dto));
    }

    // ==========================================
    // 🗑️ INATIVAR LANÇAMENTO (RH/Escritório)
    // ==========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // 🔄 REATIVAR LANÇAMENTO (RH/Escritório)
    // ==========================================
    @PostMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(@PathVariable Long id) {
        service.reativar(id);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // 💼 MÉTODOS PARA FOLHA DE PAGAMENTO
    // ==========================================

    /**
     * Soma o valor total dos benefícios de um funcionário em um mês.
     * Retorna: valorFixo + coparticipacao de todos os lançamentos.
     */
    @GetMapping("/total/funcionario/{funcionarioId}/mes/{mesReferencia}")
    public ResponseEntity<BigDecimal> somarTotalBeneficios(
            @PathVariable Long funcionarioId,
            @PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.somarTotalBeneficios(funcionarioId, mesReferencia));
    }

    /**
     * Soma apenas as coparticipações de um funcionário em um mês.
     */
    @GetMapping("/coparticipacao/funcionario/{funcionarioId}/mes/{mesReferencia}")
    public ResponseEntity<BigDecimal> somarCoparticipacoes(
            @PathVariable Long funcionarioId,
            @PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.somarCoparticipacoes(funcionarioId, mesReferencia));
    }

    /**
     * Lista todos os lançamentos de um mês específico.
     * Usado pelo painel da folha de pagamento.
     */
    @GetMapping("/folha/mes/{mesReferencia}")
    public ResponseEntity<List<LancamentoBeneficioMensalDTO>> listarLancamentosDoMes(@PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.listarLancamentosDoMes(mesReferencia));
    }

    // ==========================================
    // 📊 DASHBOARD / CONTAGENS
    // ==========================================
    @GetMapping("/dashboard/funcionario/{funcionarioId}/mes/{mesReferencia}")
    public ResponseEntity<Long> contarLancamentosPorFuncionarioEMes(
            @PathVariable Long funcionarioId,
            @PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.contarLancamentosPorFuncionarioEMes(funcionarioId, mesReferencia));
    }

    @GetMapping("/dashboard/plano/{planoBeneficioId}/mes/{mesReferencia}")
    public ResponseEntity<Long> contarLancamentosPorPlanoEMes(
            @PathVariable Long planoBeneficioId,
            @PathVariable String mesReferencia) {
        return ResponseEntity.ok(service.contarLancamentosPorPlanoEMes(planoBeneficioId, mesReferencia));
    }
}