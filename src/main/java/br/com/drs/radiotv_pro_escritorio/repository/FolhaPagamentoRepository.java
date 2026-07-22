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

    @Query("SELECT f FROM FolhaPagamento f WHERE f.funcionario.id = :funcionarioId AND f.ativa = true ORDER BY f.mesReferencia DESC")
    List<FolhaPagamento> buscarPorFuncionario(@Param("funcionarioId") Long funcionarioId);

    @Query("SELECT f FROM FolhaPagamento f WHERE f.funcionario.id = :funcionarioId AND f.statusFolha = :status AND f.ativa = true ORDER BY f.mesReferencia DESC")
    List<FolhaPagamento> buscarPorFuncionarioEStatus(@Param("funcionarioId") Long funcionarioId, @Param("status") StatusFolha status);

    @Query("SELECT f FROM FolhaPagamento f WHERE f.funcionario.id = :funcionarioId AND f.mesReferencia = :mesReferencia AND f.ativa = true")
    Optional<FolhaPagamento> buscarPorFuncionarioEMes(@Param("funcionarioId") Long funcionarioId, @Param("mesReferencia") String mesReferencia);

    @Query("SELECT f FROM FolhaPagamento f WHERE f.mesReferencia = :mesReferencia AND f.ativa = true ORDER BY f.funcionario.nome ASC")
    List<FolhaPagamento> buscarPorMesReferencia(@Param("mesReferencia") String mesReferencia);

    @Query("SELECT f FROM FolhaPagamento f WHERE f.mesReferencia = :mesReferencia AND f.statusFolha = :status AND f.ativa = true ORDER BY f.funcionario.nome ASC")
    List<FolhaPagamento> buscarPorMesReferenciaEStatus(@Param("mesReferencia") String mesReferencia, @Param("status") StatusFolha status);

    List<FolhaPagamento> findByStatusFolhaAndAtivaTrue(StatusFolha statusFolha);

    @Query("SELECT f FROM FolhaPagamento f WHERE f.statusFolha = 'FECHADA' AND f.ativa = true ORDER BY f.dataFechamento ASC")
    List<FolhaPagamento> buscarFolhasFechadas();

    @Query("SELECT f FROM FolhaPagamento f WHERE f.statusFolha = 'PAGA' AND f.dataPagamento BETWEEN :inicio AND :fim AND f.ativa = true ORDER BY f.dataPagamento DESC")
    List<FolhaPagamento> buscarFolhasPagasPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT COALESCE(SUM(f.salarioLiquido), 0) FROM FolhaPagamento f WHERE f.mesReferencia = :mesReferencia AND f.statusFolha = 'PAGA' AND f.ativa = true")
    BigDecimal somarTotalLiquidoPagoPorMes(@Param("mesReferencia") String mesReferencia);

    @Query("SELECT COALESCE(SUM(f.totalComissoes), 0) FROM FolhaPagamento f WHERE f.mesReferencia = :mesReferencia AND f.statusFolha = 'PAGA' AND f.ativa = true")
    BigDecimal somarTotalComissoesPagasPorMes(@Param("mesReferencia") String mesReferencia);

    @Query("SELECT COALESCE(SUM(f.totalBeneficios), 0) FROM FolhaPagamento f WHERE f.mesReferencia = :mesReferencia AND f.statusFolha = 'PAGA' AND f.ativa = true")
    BigDecimal somarTotalBeneficiosDescontadosPorMes(@Param("mesReferencia") String mesReferencia);

    @Query("SELECT COALESCE(SUM(f.salarioBruto), 0) FROM FolhaPagamento f WHERE f.mesReferencia = :mesReferencia AND f.ativa = true")
    BigDecimal somarTotalSalariosBrutosPorMes(@Param("mesReferencia") String mesReferencia);

    // CORREÇÃO DEFINITIVA: Usa "funcionario.id" em vez de "funcionario.id"
    boolean existsByFuncionario_IdAndMesReferenciaAndAtivaTrue(Long funcionarioId, String mesReferencia);

    @Query("SELECT f FROM FolhaPagamento f WHERE f.dataFechamento BETWEEN :inicio AND :fim AND f.ativa = true ORDER BY f.dataFechamento DESC")
    List<FolhaPagamento> buscarPorPeriodoFechamento(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT f FROM FolhaPagamento f WHERE f.dataPagamento BETWEEN :inicio AND :fim AND f.ativa = true ORDER BY f.dataPagamento DESC")
    List<FolhaPagamento> buscarPorPeriodoPagamento(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    long countByStatusFolhaAndAtivaTrue(StatusFolha statusFolha);

    // CORREÇÃO DEFINITIVA
    long countByFuncionario_IdAndAtivaTrue(Long funcionarioId);

    long countByMesReferenciaAndAtivaTrue(String mesReferencia);

    long countByMesReferenciaAndStatusFolhaAndAtivaTrue(String mesReferencia, StatusFolha statusFolha);

    @Query("SELECT f FROM FolhaPagamento f WHERE f.mesReferencia = :mesReferencia AND f.funcionario.vendedor = true AND f.ativa = true ORDER BY f.totalComissoes DESC")
    List<FolhaPagamento> buscarFolhasVendedoresPorMes(@Param("mesReferencia") String mesReferencia);

    @Query("SELECT f FROM FolhaPagamento f WHERE f.mesReferencia = :mesReferencia AND f.totalComissoes > 0 AND f.ativa = true ORDER BY f.totalComissoes DESC")
    List<FolhaPagamento> buscarFolhasComComissaoPorMes(@Param("mesReferencia") String mesReferencia);
}