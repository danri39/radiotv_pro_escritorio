package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.ContratoPagamento;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusRecebimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoPagamentoRepository extends JpaRepository<ContratoPagamento, Long>, JpaSpecificationExecutor<ContratoPagamento> {

    /**
     * Busca todas as parcelas de um contrato específico, ordenadas pelo número da parcela
     */
    List<ContratoPagamento> findByContrato_ContratoIdOrderByNumeroParcelaAsc(Long contratoId);

    /**
     * Busca parcelas por status
     */
    List<ContratoPagamento> findByStatusRecebimento(StatusRecebimento statusRecebimento);

    /**
     * Busca parcelas ativas de um contrato
     */
    List<ContratoPagamento> findByContrato_ContratoIdAndAtivoTrue(Long contratoId);

    /**
     * Busca parcelas vencidas e ainda não recebidas (para job de atraso)
     * Critério: dataVencimento < hoje E status em (A_FATURAR, FATURADO)
     */
    @Query("SELECT cp FROM ContratoPagamento cp WHERE cp.dataVencimento < :hoje " +
            "AND cp.statusRecebimento IN :statusPendentes AND cp.ativo = true")
    List<ContratoPagamento> buscarParcelasVencidasNaoRecebidas(
            @Param("hoje") LocalDate hoje,
            @Param("statusPendentes") List<StatusRecebimento> statusPendentes);

    /**
     * Busca parcelas que já foram recebidas mas ainda não tiveram comissão do vendedor lançada
     * (Usado pelo job de processamento de comissões)
     */
    @Query("SELECT cp FROM ContratoPagamento cp WHERE cp.statusRecebimento = 'RECEBIDO' " +
            "AND cp.comissaoVendedorLancada = false AND cp.ativo = true")
    List<ContratoPagamento> buscarParcelasComComissaoVendedorPendente();

    /**
     * Busca parcelas que já foram recebidas mas ainda não tiveram comissão da agência lançada
     */
    @Query("SELECT cp FROM ContratoPagamento cp WHERE cp.statusRecebimento = 'RECEBIDO' " +
            "AND cp.comissaoAgenciaLancada = false AND cp.ativo = true " +
            "AND cp.contrato.agencia IS NOT NULL")
    List<ContratoPagamento> buscarParcelasComComissaoAgenciaPendente();

    /**
     * Busca parcelas por período (para relatórios de recebimento)
     */
    @Query("SELECT cp FROM ContratoPagamento cp WHERE cp.dataPagamentoReal BETWEEN :inicio AND :fim AND cp.ativo = true")
    List<ContratoPagamento> buscarPorPeriodoRecebimento(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Busca parcelas por período de vencimento (para relatórios de inadimplência)
     */
    @Query("SELECT cp FROM ContratoPagamento cp WHERE cp.dataVencimento BETWEEN :inicio AND :fim AND cp.ativo = true")
    List<ContratoPagamento> buscarPorPeriodoVencimento(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Busca uma parcela específica de um contrato (ex: parcela 3 do contrato X)
     */
    Optional<ContratoPagamento> findByContrato_ContratoIdAndNumeroParcela(Long contratoId, Integer numeroParcela);

    /**
     * Verifica se já existe alguma parcela para um contrato (evita duplicação)
     */
    boolean existsByContrato_ContratoId(Long contratoId);

    /**
     * Busca parcelas de um vendedor específico (via contrato) para relatórios
     */
    @Query("SELECT cp FROM ContratoPagamento cp WHERE cp.contrato.vendedor.vendedorId = :vendedorId AND cp.ativo = true")
    List<ContratoPagamento> buscarPorVendedor(@Param("vendedorId") Long vendedorId);

    /**
     * Busca parcelas de uma agência específica (via contrato) para relatórios
     */
    @Query("SELECT cp FROM ContratoPagamento cp WHERE cp.contrato.agencia.agenciaId = :agenciaId AND cp.ativo = true")
    List<ContratoPagamento> buscarPorAgencia(@Param("agenciaId") Long agenciaId);
}