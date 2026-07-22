package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.FolhaPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.FolhaPagamentoMapper;
import br.com.drs.radiotv_pro_escritorio.model.FolhaPagamento;
import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusFolha;
import br.com.drs.radiotv_pro_escritorio.repository.FolhaPagamentoRepository;
import br.com.drs.radiotv_pro_escritorio.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolhaPagamentoService {

    private final FolhaPagamentoRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final FolhaPagamentoMapper mapper;
    private final ComissaoVendedorService comissaoVendedorService;
    private final LancamentoBeneficioMensalService lancamentoBeneficioService;
    private final PagamentosService pagamentosService;

    // ==========================================
    // 1. FECHAR FOLHA PARA UM FUNCIONÁRIO
    // ==========================================
    @Transactional
    public FolhaPagamentoDTO fecharFolhaFuncionario(Long funcionarioId, String mesReferencia) {
        validarFormatoMes(mesReferencia);

        if (repository.existsByFuncionario_IdAndMesReferenciaAndAtivaTrue(funcionarioId, mesReferencia)) {
            throw new RuntimeException(
                    String.format("Já existe uma folha para o funcionário %d no mês %s.", funcionarioId, mesReferencia)
            );
        }

        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + funcionarioId));

        BigDecimal salarioBruto = funcionario.getSalarioBruto() != null
                ? funcionario.getSalarioBruto()
                : BigDecimal.ZERO;

        BigDecimal totalComissoes = BigDecimal.ZERO;

        if (funcionario.getVendedor() != null && funcionario.getVendedor()) {
            totalComissoes = comissaoVendedorService.somarComissoesPendentes(funcionarioId, mesReferencia);
        }

        BigDecimal totalProventos = salarioBruto.add(totalComissoes);

        BigDecimal totalBeneficios = lancamentoBeneficioService.somarTotalBeneficios(funcionarioId, mesReferencia);

        BigDecimal totalOutrosDescontos = funcionario.getDescontosFixos() != null
                ? funcionario.getDescontosFixos()
                : BigDecimal.ZERO;

        BigDecimal totalDescontos = totalBeneficios.add(totalOutrosDescontos);
        BigDecimal salarioLiquido = totalProventos.subtract(totalDescontos);

        FolhaPagamento folha = FolhaPagamento.builder()
                .funcionario(funcionario)
                .mesReferencia(mesReferencia)
                .competencia(mesReferencia)
                .dataFechamento(LocalDate.now())
                .salarioBruto(salarioBruto)
                .totalComissoes(totalComissoes)
                .totalProventos(totalProventos)
                .totalBeneficios(totalBeneficios)
                .totalOutrosDescontos(totalOutrosDescontos)
                .totalDescontos(totalDescontos)
                .salarioLiquido(salarioLiquido)
                .statusFolha(StatusFolha.FECHADA)
                .ativa(true)
                .build();

        FolhaPagamento salva = repository.save(folha);

        log.info("Folha fechada para funcionário {} no mês {}: Líquido R$ {}",
                funcionario.getNome(), mesReferencia, salarioLiquido);

        return mapper.toDTO(salva);
    }

    // ==========================================
    // 2. FECHAR FOLHA MENSAL EM LOTE
    // ==========================================
    @Transactional
    public List<FolhaPagamentoDTO> fecharFolhaMensal(String mesReferencia) {
        validarFormatoMes(mesReferencia);

        List<Funcionario> funcionariosAtivos = funcionarioRepository.findByAtivoTrue();
        List<FolhaPagamentoDTO> folhasGeradas = new ArrayList<>();

        for (Funcionario funcionario : funcionariosAtivos) {
            try {
                FolhaPagamentoDTO folha = fecharFolhaFuncionario(funcionario.getId(), mesReferencia);
                folhasGeradas.add(folha);
            } catch (Exception e) {
                log.error("Erro ao fechar folha para funcionário {} no mês {}: {}",
                        funcionario.getNome(), mesReferencia, e.getMessage());
            }
        }

        log.info("Folha mensal {} fechada: {} folhas geradas", mesReferencia, folhasGeradas.size());
        return folhasGeradas;
    }

    // ==========================================
    // 3. LISTAGENS
    // ==========================================
    @Transactional(readOnly = true)
    public List<FolhaPagamentoDTO> listarTodas() {
        return mapper.toDTOList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public FolhaPagamentoDTO buscarPorId(Long id) {
        FolhaPagamento folha = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Folha não encontrada com ID: " + id));
        return mapper.toDTO(folha);
    }

    @Transactional(readOnly = true)
    public List<FolhaPagamentoDTO> listarPorFuncionario(Long funcionarioId) {
        return mapper.toDTOList(repository.buscarPorFuncionario(funcionarioId));
    }

    @Transactional(readOnly = true)
    public List<FolhaPagamentoDTO> listarPorMesReferencia(String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return mapper.toDTOList(repository.buscarPorMesReferencia(mesReferencia));
    }

    @Transactional(readOnly = true)
    public List<FolhaPagamentoDTO> listarPorStatus(StatusFolha status) {
        return mapper.toDTOList(repository.findByStatusFolhaAndAtivaTrue(status));
    }

    @Transactional(readOnly = true)
    public List<FolhaPagamentoDTO> listarFolhasFechadas() {
        return mapper.toDTOList(repository.buscarFolhasFechadas());
    }

    @Transactional(readOnly = true)
    public List<FolhaPagamentoDTO> listarFolhasPagasPorPeriodo(LocalDate inicio, LocalDate fim) {
        return mapper.toDTOList(repository.buscarFolhasPagasPorPeriodo(inicio, fim));
    }

    // ==========================================
    // 4. EDITAR FOLHA
    // ==========================================
    @Transactional
    public FolhaPagamentoDTO editar(Long id, FolhaPagamentoDTO dto) {
        FolhaPagamento folha = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Folha não encontrada com ID: " + id));

        if (folha.getStatusFolha() != StatusFolha.ABERTA) {
            throw new RuntimeException("Apenas folhas ABERTAS podem ser editadas. Status atual: "
                    + folha.getStatusFolha().getDescricao());
        }

        if (dto.getTotalComissoes() != null) {
            folha.setTotalComissoes(dto.getTotalComissoes());
        }
        if (dto.getTotalBeneficios() != null) {
            folha.setTotalBeneficios(dto.getTotalBeneficios());
        }
        if (dto.getTotalOutrosDescontos() != null) {
            folha.setTotalOutrosDescontos(dto.getTotalOutrosDescontos());
        }
        if (dto.getObservacao() != null) {
            folha.setObservacao(dto.getObservacao());
        }

        folha.recalcularTotais();

        FolhaPagamento salva = repository.save(folha);
        log.info("Folha {} editada. Novo líquido: R$ {}", id, folha.getSalarioLiquido());
        return mapper.toDTO(salva);
    }

    // ==========================================
    // 5. PAGAR FOLHA (COM GATILHO AUTOMÁTICO)
    // ==========================================
    @Transactional
    public FolhaPagamentoDTO pagarFolha(Long id, String formaPagamento) {
        FolhaPagamento folha = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Folha não encontrada com ID: " + id));

        if (folha.getStatusFolha() != StatusFolha.FECHADA) {
            throw new RuntimeException("Apenas folhas FECHADAS podem ser pagas. Status atual: "
                    + folha.getStatusFolha().getDescricao());
        }

        // GATILHO: Lança a despesa de salário no módulo de Pagamentos
        Long pagamentoId = pagamentosService.lancarPagamentoFolha(folha);

        folha.marcarComoPaga(LocalDate.now(), formaPagamento, pagamentoId);
        FolhaPagamento salva = repository.save(folha);

        log.info("Folha {} paga. Valor líquido: R$ {} via {}",
                id, folha.getSalarioLiquido(), formaPagamento);

        return mapper.toDTO(salva);
    }

    // ==========================================
    // 6. CANCELAR FOLHA
    // ==========================================
    @Transactional
    public void cancelar(Long id, String motivo) {
        FolhaPagamento folha = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Folha não encontrada com ID: " + id));

        folha.cancelar();
        repository.save(folha);
        log.info("Folha {} cancelada. Motivo: {}", id, motivo);
    }

    // ==========================================
    // 7. RELATÓRIOS
    // ==========================================
    @Transactional(readOnly = true)
    public BigDecimal somarTotalLiquidoPagoPorMes(String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return repository.somarTotalLiquidoPagoPorMes(mesReferencia);
    }

    @Transactional(readOnly = true)
    public BigDecimal somarTotalComissoesPagasPorMes(String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return repository.somarTotalComissoesPagasPorMes(mesReferencia);
    }

    @Transactional(readOnly = true)
    public BigDecimal somarTotalBeneficiosDescontadosPorMes(String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return repository.somarTotalBeneficiosDescontadosPorMes(mesReferencia);
    }

    @Transactional(readOnly = true)
    public List<FolhaPagamentoDTO> listarFolhasVendedoresPorMes(String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return mapper.toDTOList(repository.buscarFolhasVendedoresPorMes(mesReferencia));
    }

    // ==========================================
    // 8. DASHBOARD
    // ==========================================
    @Transactional(readOnly = true)
    public long contarFolhasPorStatus(StatusFolha status) {
        return repository.countByStatusFolhaAndAtivaTrue(status);
    }

    @Transactional(readOnly = true)
    public long contarFolhasPorMes(String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return repository.countByMesReferenciaAndAtivaTrue(mesReferencia);
    }

    // ==========================================
    // HELPER
    // ==========================================
    private void validarFormatoMes(String mesReferencia) {
        if (mesReferencia == null || !mesReferencia.matches("\\d{2}/\\d{4}")) {
            throw new RuntimeException("Formato de mês inválido. Use 'MM/yyyy' (ex: '07/2026').");
        }
    }
}