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

    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.funcionario.id = :funcionarioId AND l.ativo = true ORDER BY l.mesReferencia DESC")
    List<LancamentoBeneficioMensal> buscarPorFuncionario(@Param("funcionarioId") Long funcionarioId);

    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.funcionario.id = :funcionarioId AND l.mesReferencia = :mesReferencia AND l.ativo = true")
    List<LancamentoBeneficioMensal> buscarPorFuncionarioEMes(@Param("funcionarioId") Long funcionarioId, @Param("mesReferencia") String mesReferencia);

    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.funcionario.id = :funcionarioId AND l.planoBeneficio.planoBeneficioId = :planoBeneficioId AND l.ativo = true ORDER BY l.mesReferencia DESC")
    List<LancamentoBeneficioMensal> buscarPorFuncionarioEPlano(@Param("funcionarioId") Long funcionarioId, @Param("planoBeneficioId") Long planoBeneficioId);

    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.mesReferencia = :mesReferencia AND l.ativo = true")
    List<LancamentoBeneficioMensal> buscarPorMesReferencia(@Param("mesReferencia") String mesReferencia);

    @Query("SELECT COALESCE(SUM(l.planoBeneficio.valorMensalFixo + COALESCE(l.valorCoparticipacao, 0)), 0) FROM LancamentoBeneficioMensal l WHERE l.funcionario.id = :funcionarioId AND l.mesReferencia = :mesReferencia AND l.ativo = true")
    BigDecimal somarTotalBeneficiosPorFuncionarioEMes(@Param("funcionarioId") Long funcionarioId, @Param("mesReferencia") String mesReferencia);

    @Query("SELECT COALESCE(SUM(l.valorCoparticipacao), 0) FROM LancamentoBeneficioMensal l WHERE l.funcionario.id = :funcionarioId AND l.mesReferencia = :mesReferencia AND l.ativo = true")
    BigDecimal somarCoparticipacoesPorFuncionarioEMes(@Param("funcionarioId") Long funcionarioId, @Param("mesReferencia") String mesReferencia);

    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.dataLancamento BETWEEN :inicio AND :fim AND l.ativo = true ORDER BY l.dataLancamento DESC")
    List<LancamentoBeneficioMensal> buscarPorPeriodoLancamento(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.funcionario.id = :funcionarioId AND l.dataLancamento BETWEEN :inicio AND :fim AND l.ativo = true ORDER BY l.dataLancamento DESC")
    List<LancamentoBeneficioMensal> buscarPorFuncionarioEPeriodo(@Param("funcionarioId") Long funcionarioId, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // CORREÇÃO: Usa "Funcionario_Id" em vez de "Funcionario_FuncionarioId"
    boolean existsByFuncionario_IdAndPlanoBeneficio_PlanoBeneficioIdAndMesReferenciaAndAtivoTrue(Long funcionarioId, Long planoBeneficioId, String mesReferencia);

    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.funcionario.id = :funcionarioId AND l.planoBeneficio.planoBeneficioId = :planoBeneficioId AND l.mesReferencia = :mesReferencia AND l.ativo = true")
    Optional<LancamentoBeneficioMensal> buscarPorFuncionarioPlanoEMes(@Param("funcionarioId") Long funcionarioId, @Param("planoBeneficioId") Long planoBeneficioId, @Param("mesReferencia") String mesReferencia);

    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.planoBeneficio.planoBeneficioId = :planoBeneficioId AND l.mesReferencia = :mesReferencia AND l.ativo = true")
    List<LancamentoBeneficioMensal> buscarPorPlanoEMes(@Param("planoBeneficioId") Long planoBeneficioId, @Param("mesReferencia") String mesReferencia);

    @Query("SELECT l FROM LancamentoBeneficioMensal l WHERE l.planoBeneficio.planoBeneficioId = :planoBeneficioId AND l.dataLancamento BETWEEN :inicio AND :fim AND l.ativo = true ORDER BY l.dataLancamento DESC")
    List<LancamentoBeneficioMensal> buscarPorPlanoEPeriodo(@Param("planoBeneficioId") Long planoBeneficioId, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // CORREÇÃO: Usa "Funcionario_Id"
    long countByFuncionario_IdAndMesReferenciaAndAtivoTrue(Long funcionarioId, String mesReferencia);

    long countByPlanoBeneficio_PlanoBeneficioIdAndMesReferenciaAndAtivoTrue(Long planoBeneficioId, String mesReferencia);
}