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

    // Services integrados
    private final ComissaoVendedorService comissaoVendedorService;
    private final LancamentoBeneficioMensalService lancamentoBeneficioService;
    private final PagamentosService pagamentosService;

    // ==========================================
    // 1. FECHAR FOLHA PARA UM FUNCIONÁRIO (RH/Escritório)
    // ==========================================
    /**
     * Fecha a folha de pagamento para um funcionário específico em um mês.
     * O sistema:
     * 1. Busca o salário bruto do Funcionario
     * 2. Busca comissões pendentes do vendedor (se aplicável) e processa
     * 3. Busca benefícios do mês e soma
     * 4. Aplica outros descontos (VT, VR, INSS, IR) - TODO: implementar cálculo
     * 5. Cria a FolhaPagamento com status = FECHADA
     */
    @Transactional
    public FolhaPagamentoDTO fecharFolhaFuncionario(Long funcionarioId, String mesReferencia) {
        validarFormatoMes(mesReferencia);

        // TRAVA: Evita duplicação
        if (repository.existsByFuncionario_FuncionarioIdAndMesReferenciaAndAtivaTrue(funcionarioId, mesReferencia)) {
            throw new RuntimeException(
                    String.format("Já existe uma folha para o funcionário %d no mês %s.", funcionarioId, mesReferencia)
            );
        }

        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + funcionarioId));

        // ==========================================
        // CALCULAR PROVENTOS
        // ==========================================
        BigDecimal salarioBruto = funcionario.getSalarioBruto() != null
                ? funcionario.getSalarioBruto()
                : BigDecimal.ZERO;

        BigDecimal totalComissoes = BigDecimal.ZERO;

        // Se o funcionário é vendedor, processa as comissões
        if (funcionario.getVendedor() != null && funcionario.getVendedor()) {
            // Busca o vendedor vinculado ao funcionário
            // TODO: Buscar Vendedor pelo funcionarioId (precisa de VendedorRepository)
            // Por enquanto, vamos assumir que o ComissaoVendedorService tem um método para buscar por funcionarioId

            // Soma as comissões pendentes do mês
            totalComissoes = comissaoVendedorService.somarComissoesPendentes(funcionarioId, mesReferencia);

            // Processa as comissões (marca como PROCESSADA e vincula à folha)
            // TODO: Precisa do vendedorId para chamar processarEmLote
            // comissaoVendedorService.processarEmLote(vendedorId, mesReferencia, folhaId);
        }

        BigDecimal totalProventos = salarioBruto.add(totalComissoes);

        // ==========================================
        // CALCULAR DESCONTOS
        // ==========================================

        // Busca benefícios do mês (plano de saúde + coparticipações)
        BigDecimal totalBeneficios = lancamentoBeneficioService.somarTotalBeneficios(funcionarioId, mesReferencia);

        // TODO: Calcular outros descontos (VT, VR, INSS, IR)
        // Por enquanto, vamos usar os descontosFixos do Funcionario
        BigDecimal totalOutrosDescontos = funcionario.getDescontosFixos() != null
                ? funcionario.getDescontosFixos()
                : BigDecimal.ZERO;

        BigDecimal totalDescontos = totalBeneficios.add(totalOutrosDescontos);

        // ==========================================
        // CALCULAR SALÁRIO LÍQUIDO
        // ==========================================
        BigDecimal salarioLiquido = totalProventos.subtract(totalDescontos);

        // ==========================================
        // CRIAR A FOLHA
        // ==========================================
        FolhaPagamento folha = FolhaPagamento.builder()
                .funcionario(funcionario)
                .mesReferencia(mesReferencia)
                .competencia(mesReferencia) // Por enquanto, competência = mês de referência
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
    // 2. FECHAR FOLHA PARA TODOS OS FUNCIONÁRIOS (Processo em Lote)
    // ==========================================
    /**
     * Fecha a folha de todos os funcionários ativos em um mês.
     * Usado pelo RH no fechamento mensal.
     */
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
                // Continua processando os outros funcionários
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
    // 4. EDITAR FOLHA (Apenas quando ABERTA)
    // ==========================================
    /**
     * Permite editar valores da folha (ex: ajustar comissões, benefícios, outros descontos).
     * Só pode ser editada se status = ABERTA.
     * Após edição, recalcula os totais automaticamente.
     */
    @Transactional
    public FolhaPagamentoDTO editar(Long id, FolhaPagamentoDTO dto) {
        FolhaPagamento folha = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Folha não encontrada com ID: " + id));

        if (folha.getStatusFolha() != StatusFolha.ABERTA) {
            throw new RuntimeException("Apenas folhas ABERTAS podem ser editadas. Status atual: "
                    + folha.getStatusFolha().getDescricao());
        }

        // Atualiza valores (permitindo ajustes manuais)
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

        // Recalcula os totais
        folha.recalcularTotais();

        FolhaPagamento salva = repository.save(folha);

        log.info("Folha {} editada. Novo líquido: R$ {}", id, folha.getSalarioLiquido());

        return mapper.toDTO(salva);
    }

    // ==========================================
    // 5. PAGAR FOLHA (Escritório)
    // ==========================================
    /**
     * Marca a folha como PAGA e gera um lançamento no módulo de Pagamentos.
     */
    @Transactional
    public FolhaPagamentoDTO pagarFolha(Long id, String formaPagamento) {
        FolhaPagamento folha = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Folha não encontrada com ID: " + id));

        if (folha.getStatusFolha() != StatusFolha.FECHADA) {
            throw new RuntimeException("Apenas folhas FECHADAS podem ser pagas. Status atual: "
                    + folha.getStatusFolha().getDescricao());
        }

        // TODO: Criar lançamento em Pagamentos (tipo = SALARIO)
        // PagamentosDTO pagamento = pagamentosService.lancarPagamentoFolha(folha);
        // Long pagamentoId = pagamento.getPagamentoId();

        // Por enquanto, vamos apenas marcar como paga sem gerar o pagamento
        Long pagamentoId = null; // TODO: substituir pelo ID real do pagamento

        folha.marcarComoPaga(LocalDate.now(), formaPagamento, pagamentoId);
        FolhaPagamento salva = repository.save(folha);

        log.info("Folha {} paga. Valor líquido: R$ {} via {}",
                id, folha.getSalarioLiquido(), formaPagamento);

        return mapper.toDTO(salva);
    }

    // ==========================================
    // 6. CANCELAR FOLHA (Administrador)
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
    // 7. RELATÓRIOS FINANCEIROS
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
    // 8. CONTAGENS PARA DASHBOARD
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
    // MÉTODO HELPER: Validação do formato do mês
    // ==========================================
    private void validarFormatoMes(String mesReferencia) {
        if (mesReferencia == null || !mesReferencia.matches("\\d{2}/\\d{4}")) {
            throw new RuntimeException("Formato de mês inválido. Use 'MM/yyyy' (ex: '07/2026').");
        }
    }
}