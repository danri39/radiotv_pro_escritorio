package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.LancamentoBeneficioMensal;
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
public interface LancamentoBeneficioMensalRepository extends JpaRepository<LancamentoBeneficioMensal, Long>, JpaSpecificationExecutor<LancamentoBeneficioMensal> {

    // ==========================================
    // BUSCAS POR FUNCIONÁRIO
    // ==========================================

    /**
     * Lista todos os lançamentos de um funcionário específico
     */
    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.funcionario.funcionarioId = :funcionarioId " +
            "AND l.ativo = true ORDER BY l.mesReferencia DESC")
    List<LancamentoBeneficioMensal> buscarPorFuncionario(@Param("funcionarioId") Long funcionarioId);

    /**
     * Lista lançamentos de um funcionário em um mês específico
     */
    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.funcionario.funcionarioId = :funcionarioId " +
            "AND l.mesReferencia = :mesReferencia AND l.ativo = true")
    List<LancamentoBeneficioMensal> buscarPorFuncionarioEMes(
            @Param("funcionarioId") Long funcionarioId,
            @Param("mesReferencia") String mesReferencia);

    /**
     * Lista lançamentos de um funcionário em um plano específico
     */
    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.funcionario.funcionarioId = :funcionarioId " +
            "AND l.planoBeneficio.planoBeneficioId = :planoBeneficioId AND l.ativo = true " +
            "ORDER BY l.mesReferencia DESC")
    List<LancamentoBeneficioMensal> buscarPorFuncionarioEPlano(
            @Param("funcionarioId") Long funcionarioId,
            @Param("planoBeneficioId") Long planoBeneficioId);

    // ==========================================
    // BUSCAS PARA FOLHA DE PAGAMENTO
    // ==========================================

    /**
     * Lista todos os lançamentos de um mês de referência (de todos os funcionários).
     * Usado pelo FolhaPagamentoService para fechar a folha.
     */
    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.mesReferencia = :mesReferencia AND l.ativo = true")
    List<LancamentoBeneficioMensal> buscarPorMesReferencia(@Param("mesReferencia") String mesReferencia);

    /**
     * Soma o valor total dos benefícios de um funcionário em um mês.
     * Usado para calcular o total a descontar na folha.
     * Retorna a soma de (valorFixo + coparticipacao) de todos os lançamentos.
     */
    @Query("SELECT COALESCE(SUM(l.planoBeneficio.valorMensalFixo + COALESCE(l.valorCoparticipacao, 0)), 0) " +
            "FROM LancamentoBeneficioMensal l WHERE l.funcionario.funcionarioId = :funcionarioId " +
            "AND l.mesReferencia = :mesReferencia AND l.ativo = true")
    BigDecimal somarTotalBeneficiosPorFuncionarioEMes(
            @Param("funcionarioId") Long funcionarioId,
            @Param("mesReferencia") String mesReferencia);

    /**
     * Soma apenas as coparticipações de um funcionário em um mês.
     * Usado para relatórios específicos de coparticipação.
     */
    @Query("SELECT COALESCE(SUM(l.valorCoparticipacao), 0) FROM LancamentoBeneficioMensal l " +
            "WHERE l.funcionario.funcionarioId = :funcionarioId AND l.mesReferencia = :mesReferencia AND l.ativo = true")
    BigDecimal somarCoparticipacoesPorFuncionarioEMes(
            @Param("funcionarioId") Long funcionarioId,
            @Param("mesReferencia") String mesReferencia);

    // ==========================================
    // BUSCAS POR PERÍODO (relatórios)
    // ==========================================

    /**
     * Lista lançamentos por período de data de lançamento
     */
    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.dataLancamento BETWEEN :inicio AND :fim AND l.ativo = true " +
            "ORDER BY l.dataLancamento DESC")
    List<LancamentoBeneficioMensal> buscarPorPeriodoLancamento(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Lista lançamentos de um funcionário por período
     */
    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.funcionario.funcionarioId = :funcionarioId " +
            "AND l.dataLancamento BETWEEN :inicio AND :fim AND l.ativo = true " +
            "ORDER BY l.dataLancamento DESC")
    List<LancamentoBeneficioMensal> buscarPorFuncionarioEPeriodo(
            @Param("funcionarioId") Long funcionarioId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    // ==========================================
    // CONTROLE DE DUPLICAÇÃO
    // ==========================================

    /**
     * Verifica se já existe lançamento para um funcionário, plano e mês específico.
     * Evita duplicação quando o RH lança coparticipações.
     */
    boolean existsByFuncionario_FuncionarioIdAndPlanoBeneficio_PlanoBeneficioIdAndMesReferenciaAndAtivoTrue(
            Long funcionarioId,
            Long planoBeneficioId,
            String mesReferencia);

    /**
     * Busca um lançamento específico (funcionário + plano + mês)
     */
    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.funcionario.funcionarioId = :funcionarioId " +
            "AND l.planoBeneficio.planoBeneficioId = :planoBeneficioId AND l.mesReferencia = :mesReferencia AND l.ativo = true")
    Optional<LancamentoBeneficioMensal> buscarPorFuncionarioPlanoEMes(
            @Param("funcionarioId") Long funcionarioId,
            @Param("planoBeneficioId") Long planoBeneficioId,
            @Param("mesReferencia") String mesReferencia);

    // ==========================================
    // BUSCAS POR PLANO (relatórios de operadora)
    // ==========================================

    /**
     * Lista lançamentos de um plano específico em um mês
     */
    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.planoBeneficio.planoBeneficioId = :planoBeneficioId " +
            "AND l.mesReferencia = :mesReferencia AND l.ativo = true")
    List<LancamentoBeneficioMensal> buscarPorPlanoEMes(
            @Param("planoBeneficioId") Long planoBeneficioId,
            @Param("mesReferencia") String mesReferencia);

    /**
     * Lista lançamentos de um plano específico por período
     */
    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.planoBeneficio.planoBeneficioId = :planoBeneficioId " +
            "AND l.dataLancamento BETWEEN :inicio AND :fim AND l.ativo = true " +
            "ORDER BY l.dataLancamento DESC")
    List<LancamentoBeneficioMensal> buscarPorPlanoEPeriodo(
            @Param("planoBeneficioId") Long planoBeneficioId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    // ==========================================
    // CONTAGENS PARA DASHBOARD
    // ==========================================

    /**
     * Conta lançamentos de um funcionário em um mês
     */
    long countByFuncionario_FuncionarioIdAndMesReferenciaAndAtivoTrue(Long funcionarioId, String mesReferencia);

    /**
     * Conta lançamentos de um plano em um mês
     */
    long countByPlanoBeneficio_PlanoBeneficioIdAndMesReferenciaAndAtivoTrue(Long planoBeneficioId, String mesReferencia);
}