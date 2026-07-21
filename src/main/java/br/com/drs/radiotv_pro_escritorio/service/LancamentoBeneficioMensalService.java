package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.LancamentoBeneficioMensalDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.LancamentoBeneficioMensalMapper;
import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import br.com.drs.radiotv_pro_escritorio.model.LancamentoBeneficioMensal;
import br.com.drs.radiotv_pro_escritorio.model.PlanoBeneficio;
import br.com.drs.radiotv_pro_escritorio.repository.FuncionarioRepository;
import br.com.drs.radiotv_pro_escritorio.repository.LancamentoBeneficioMensalRepository;
import br.com.drs.radiotv_pro_escritorio.repository.PlanoBeneficioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LancamentoBeneficioMensalService {

    private final LancamentoBeneficioMensalRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final PlanoBeneficioRepository planoBeneficioRepository;
    private final LancamentoBeneficioMensalMapper mapper;

    // ==========================================
    // 1. CRIAR LANÇAMENTO MENSAL (RH/Escritório)
    // ==========================================
    @Transactional
    public LancamentoBeneficioMensalDTO criar(LancamentoBeneficioMensalDTO dto) {
        validarDadosBasicos(dto);
        validarFormatoMes(dto.getMesReferencia());

        // Busca o funcionário
        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + dto.getFuncionarioId()));

        // Busca o plano de benefício
        PlanoBeneficio plano = planoBeneficioRepository.findById(dto.getPlanoBeneficioId())
                .orElseThrow(() -> new RuntimeException("Plano de benefício não encontrado com ID: " + dto.getPlanoBeneficioId()));

        // TRAVA: Evita duplicação (funcionário + plano + mês)
        if (repository.existsByFuncionario_FuncionarioIdAndPlanoBeneficio_PlanoBeneficioIdAndMesReferenciaAndAtivoTrue(
                dto.getFuncionarioId(), dto.getPlanoBeneficioId(), dto.getMesReferencia())) {
            throw new RuntimeException(
                    String.format("Já existe um lançamento para o funcionário %s no plano %s no mês %s. " +
                                    "Use a edição para atualizar os valores.",
                            funcionario.getNome(), plano.getNome(), dto.getMesReferencia())
            );
        }

        LancamentoBeneficioMensal lancamento = mapper.toEntity(dto);
        lancamento.setFuncionario(funcionario);
        lancamento.setPlanoBeneficio(plano);
        lancamento.setDataLancamento(LocalDate.now());
        lancamento.setAtivo(true);

        // Se não informou coparticipação, assume zero
        if (lancamento.getValorCoparticipacao() == null) {
            lancamento.setValorCoparticipacao(BigDecimal.ZERO);
        }

        LancamentoBeneficioMensal salvo = repository.save(lancamento);

        log.info("Lançamento de benefício criado: {} - {} - Mês {} - Total: R$ {}",
                funcionario.getNome(),
                plano.getNome(),
                dto.getMesReferencia(),
                salvo.calcularTotalMensal());

        return mapper.toDTO(salvo);
    }

    // ==========================================
    // 2. LISTAGENS
    // ==========================================
    @Transactional(readOnly = true)
    public List<LancamentoBeneficioMensalDTO> listarTodos() {
        return mapper.toDTOList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public LancamentoBeneficioMensalDTO buscarPorId(Long id) {
        LancamentoBeneficioMensal lancamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado com ID: " + id));
        return mapper.toDTO(lancamento);
    }

    @Transactional(readOnly = true)
    public List<LancamentoBeneficioMensalDTO> listarPorFuncionario(Long funcionarioId) {
        return mapper.toDTOList(repository.buscarPorFuncionario(funcionarioId));
    }

    @Transactional(readOnly = true)
    public List<LancamentoBeneficioMensalDTO> listarPorFuncionarioEMes(Long funcionarioId, String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return mapper.toDTOList(repository.buscarPorFuncionarioEMes(funcionarioId, mesReferencia));
    }

    @Transactional(readOnly = true)
    public List<LancamentoBeneficioMensalDTO> listarPorMesReferencia(String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return mapper.toDTOList(repository.buscarPorMesReferencia(mesReferencia));
    }

    @Transactional(readOnly = true)
    public List<LancamentoBeneficioMensalDTO> listarPorPeriodoLancamento(LocalDate inicio, LocalDate fim) {
        return mapper.toDTOList(repository.buscarPorPeriodoLancamento(inicio, fim));
    }

    // ==========================================
    // 3. EDITAR LANÇAMENTO (RH/Escritório)
    // ==========================================
    @Transactional
    public LancamentoBeneficioMensalDTO editar(Long id, LancamentoBeneficioMensalDTO dto) {
        LancamentoBeneficioMensal lancamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado com ID: " + id));

        validarDadosBasicos(dto);

        // Atualiza apenas os campos que podem ser editados
        lancamento.setValorCoparticipacao(dto.getValorCoparticipacao() != null ? dto.getValorCoparticipacao() : BigDecimal.ZERO);
        lancamento.setQuantidadeUtilizacoes(dto.getQuantidadeUtilizacoes());
        lancamento.setDescricaoUtilizacoes(dto.getDescricaoUtilizacoes());
        lancamento.setObservacao(dto.getObservacao());

        LancamentoBeneficioMensal salvo = repository.save(lancamento);

        log.info("Lançamento de benefício atualizado: ID {} - Nova coparticipação: R$ {}",
                id, lancamento.getValorCoparticipacao());

        return mapper.toDTO(salvo);
    }

    // ==========================================
    // 4. INATIVAR LANÇAMENTO (Soft Delete)
    // ==========================================
    @Transactional
    public void inativar(Long id) {
        LancamentoBeneficioMensal lancamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado com ID: " + id));

        lancamento.setAtivo(false);
        repository.save(lancamento);
        log.info("Lançamento de benefício inativado: ID {}", id);
    }

    // ==========================================
    // 5. REATIVAR LANÇAMENTO
    // ==========================================
    @Transactional
    public void reativar(Long id) {
        LancamentoBeneficioMensal lancamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado com ID: " + id));

        lancamento.setAtivo(true);
        repository.save(lancamento);
        log.info("Lançamento de benefício reativado: ID {}", id);
    }

    // ==========================================
    // 6. MÉTODOS PARA FOLHA DE PAGAMENTO
    // ==========================================

    /**
     * Soma o valor total dos benefícios de um funcionário em um mês.
     * Usado pelo FolhaPagamentoService para calcular o total a descontar.
     */
    @Transactional(readOnly = true)
    public BigDecimal somarTotalBeneficios(Long funcionarioId, String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return repository.somarTotalBeneficiosPorFuncionarioEMes(funcionarioId, mesReferencia);
    }

    /**
     * Soma apenas as coparticipações de um funcionário em um mês.
     * Usado para relatórios específicos.
     */
    @Transactional(readOnly = true)
    public BigDecimal somarCoparticipacoes(Long funcionarioId, String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return repository.somarCoparticipacoesPorFuncionarioEMes(funcionarioId, mesReferencia);
    }

    /**
     * Lista todos os lançamentos de um mês (para o painel da folha).
     */
    @Transactional(readOnly = true)
    public List<LancamentoBeneficioMensalDTO> listarLancamentosDoMes(String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return mapper.toDTOList(repository.buscarPorMesReferencia(mesReferencia));
    }

    // ==========================================
    // 7. CONTAGENS PARA DASHBOARD
    // ==========================================
    @Transactional(readOnly = true)
    public long contarLancamentosPorFuncionarioEMes(Long funcionarioId, String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return repository.countByFuncionario_FuncionarioIdAndMesReferenciaAndAtivoTrue(funcionarioId, mesReferencia);
    }

    @Transactional(readOnly = true)
    public long contarLancamentosPorPlanoEMes(Long planoBeneficioId, String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return repository.countByPlanoBeneficio_PlanoBeneficioIdAndMesReferenciaAndAtivoTrue(planoBeneficioId, mesReferencia);
    }

    // ==========================================
    // MÉTODOS HELPERS: Validações
    // ==========================================
    private void validarDadosBasicos(LancamentoBeneficioMensalDTO dto) {
        if (dto.getFuncionarioId() == null) {
            throw new RuntimeException("O funcionário é obrigatório.");
        }
        if (dto.getPlanoBeneficioId() == null) {
            throw new RuntimeException("O plano de benefício é obrigatório.");
        }
        if (dto.getMesReferencia() == null || dto.getMesReferencia().isBlank()) {
            throw new RuntimeException("O mês de referência é obrigatório.");
        }
        if (dto.getTipoBeneficiario() == null) {
            throw new RuntimeException("O tipo de beneficiário é obrigatório.");
        }
    }

    private void validarFormatoMes(String mesReferencia) {
        if (mesReferencia == null || !mesReferencia.matches("\\d{2}/\\d{4}")) {
            throw new RuntimeException("Formato de mês inválido. Use 'MM/yyyy' (ex: '07/2026').");
        }
    }
}