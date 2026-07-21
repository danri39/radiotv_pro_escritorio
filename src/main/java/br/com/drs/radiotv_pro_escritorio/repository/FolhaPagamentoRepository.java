package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.FolhaPagamento;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusFolha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FolhaPagamentoRepository extends JpaRepository<FolhaPagamento, Long>, JpaSpecificationExecutor<FolhaPagamento> {

    // ==========================================
    // BUSCAS POR FUNCIONÁRIO
    // ==========================================

    /**
     * Lista todas as folhas de um funcionário específico, ordenadas por mês (mais recente primeiro)
     */
    @Query("SELECT f FROM FolhaPagamento f WHERE f.funcionario.funcionarioId = :funcionarioId AND f.ativa = true " +
            "ORDER BY f.mesReferencia DESC")
    List<FolhaPagamento> buscarPorFuncionario(@Param("funcionarioId") Long funcionarioId);

    /**
     * Lista folhas de um funcionário por status
     */
    @Query("SELECT f FROM FolhaPagamento f WHERE f.funcionario.funcionarioId = :funcionarioId " +
            "AND f.statusFolha = :status AND f.ativa = true ORDER BY f.mesReferencia DESC")
    List<FolhaPagamento> buscarPorFuncionarioEStatus(
            @Param("funcionarioId") Long funcionarioId,
            @Param("status") StatusFolha status);

    /**
     * Busca uma folha específica de um funcionário em um mês de referência
     */
    @Query("SELECT f FROM FolhaPagamento f WHERE f.funcionario.funcionarioId = :funcionarioId " +
            "AND f.mesReferencia = :mesReferencia AND f.ativa = true")
    Optional<FolhaPagamento> buscarPorFuncionarioEMes(
            @Param("funcionarioId") Long funcionarioId,
            @Param("mesReferencia") String mesReferencia);

    // ==========================================
    // BUSCAS POR MÊS DE REFERÊNCIA
    // ==========================================

    /**
     * Lista todas as folhas de um mês de referência específico (de todos os funcionários)
     * Usado pelo painel da folha de pagamento
     */
    @Query("SELECT f FROM FolhaPagamento f WHERE f.mesReferencia = :mesReferencia AND f.ativa = true " +
            "ORDER BY f.funcionario.nome ASC")
    List<FolhaPagamento> buscarPorMesReferencia(@Param("mesReferencia") String mesReferencia);

    /**
     * Lista folhas de um mês por status
     */
    @Query("SELECT f FROM FolhaPagamento f WHERE f.mesReferencia = :mesReferencia " +
            "AND f.statusFolha = :status AND f.ativa = true ORDER BY f.funcionario.nome ASC")
    List<FolhaPagamento> buscarPorMesReferenciaEStatus(
            @Param("mesReferencia") String mesReferencia,
            @Param("status") StatusFolha status);

    // ==========================================
    // BUSCAS POR STATUS
    // ==========================================

    /**
     * Lista todas as folhas com um status específico
     */
    List<FolhaPagamento> findByStatusFolhaAndAtivaTrue(StatusFolha statusFolha);

    /**
     * Lista folhas fechadas (aguardando pagamento)
     */
    @Query("SELECT f FROM FolhaPagamento f WHERE f.statusFolha = 'FECHADA' AND f.ativa = true " +
            "ORDER BY f.dataFechamento ASC")
    List<FolhaPagamento> buscarFolhasFechadas();

    /**
     * Lista folhas pagas em um período
     */
    @Query("SELECT f FROM FolhaPagamento f WHERE f.statusFolha = 'PAGA' " +
            "AND f.dataPagamento BETWEEN :inicio AND :fim AND f.ativa = true " +
            "ORDER BY f.dataPagamento DESC")
    List<FolhaPagamento> buscarFolhasPagasPorPeriodo(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    // ==========================================
    // SOMATÓRIOS PARA RELATÓRIOS FINANCEIROS
    // ==========================================

    /**
     * Soma o total de salários líquidos pagos em um mês
     */
    @Query("SELECT COALESCE(SUM(f.salarioLiquido), 0) FROM FolhaPagamento f " +
            "WHERE f.mesReferencia = :mesReferencia AND f.statusFolha = 'PAGA' AND f.ativa = true")
    BigDecimal somarTotalLiquidoPagoPorMes(@Param("mesReferencia") String mesReferencia);

    /**
     * Soma o total de comissões pagas em um mês
     */
    @Query("SELECT COALESCE(SUM(f.totalComissoes), 0) FROM FolhaPagamento f " +
            "WHERE f.mesReferencia = :mesReferencia AND f.statusFolha = 'PAGA' AND f.ativa = true")
    BigDecimal somarTotalComissoesPagasPorMes(@Param("mesReferencia") String mesReferencia);

    /**
     * Soma o total de benefícios descontados em um mês
     */
    @Query("SELECT COALESCE(SUM(f.totalBeneficios), 0) FROM FolhaPagamento f " +
            "WHERE f.mesReferencia = :mesReferencia AND f.statusFolha = 'PAGA' AND f.ativa = true")
    BigDecimal somarTotalBeneficiosDescontadosPorMes(@Param("mesReferencia") String mesReferencia);

    /**
     * Soma o total de salários brutos de um mês
     */
    @Query("SELECT COALESCE(SUM(f.salarioBruto), 0) FROM FolhaPagamento f " +
            "WHERE f.mesReferencia = :mesReferencia AND f.ativa = true")
    BigDecimal somarTotalSalariosBrutosPorMes(@Param("mesReferencia") String mesReferencia);

    // ==========================================
    // CONTROLE DE DUPLICAÇÃO
    // ==========================================

    /**
     * Verifica se já existe folha para um funcionário em um mês específico
     * Evita duplicação ao fechar folha
     */
    boolean existsByFuncionario_FuncionarioIdAndMesReferenciaAndAtivaTrue(
            Long funcionarioId,
            String mesReferencia);

    // ==========================================
    // BUSCAS POR PERÍODO (relatórios)
    // ==========================================

    /**
     * Lista folhas por período de fechamento
     */
    @Query("SELECT f FROM FolhaPagamento f WHERE f.dataFechamento BETWEEN :inicio AND :fim AND f.ativa = true " +
            "ORDER BY f.dataFechamento DESC")
    List<FolhaPagamento> buscarPorPeriodoFechamento(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Lista folhas por período de pagamento
     */
    @Query("SELECT f FROM FolhaPagamento f WHERE f.dataPagamento BETWEEN :inicio AND :fim AND f.ativa = true " +
            "ORDER BY f.dataPagamento DESC")
    List<FolhaPagamento> buscarPorPeriodoPagamento(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    // ==========================================
    // CONTAGENS PARA DASHBOARD
    // ==========================================

    /**
     * Conta folhas por status
     */
    long countByStatusFolhaAndAtivaTrue(StatusFolha statusFolha);

    /**
     * Conta folhas de um funcionário
     */
    long countByFuncionario_FuncionarioIdAndAtivaTrue(Long funcionarioId);

    /**
     * Conta folhas de um mês
     */
    long countByMesReferenciaAndAtivaTrue(String mesReferencia);

    /**
     * Conta folhas pagas de um mês
     */
    long countByMesReferenciaAndStatusFolhaAndAtivaTrue(String mesReferencia, StatusFolha statusFolha);

    // ==========================================
    // BUSCAS PARA RELATÓRIOS DE COMISSÕES
    // ==========================================

    /**
     * Lista folhas de vendedores (funcionarios com vendedor = true) em um mês
     * Usado para relatório de comissões pagas
     */
    @Query("SELECT f FROM FolhaPagamento f WHERE f.mesReferencia = :mesReferencia " +
            "AND f.funcionario.vendedor = true AND f.ativa = true " +
            "ORDER BY f.totalComissoes DESC")
    List<FolhaPagamento> buscarFolhasVendedoresPorMes(@Param("mesReferencia") String mesReferencia);

    /**
     * Lista folhas com comissões maiores que zero em um mês
     */
    @Query("SELECT f FROM FolhaPagamento f WHERE f.mesReferencia = :mesReferencia " +
            "AND f.totalComissoes > 0 AND f.ativa = true " +
            "ORDER BY f.totalComissoes DESC")
    List<FolhaPagamento> buscarFolhasComComissaoPorMes(@Param("mesReferencia") String mesReferencia);
}