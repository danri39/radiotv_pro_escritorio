package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.Pagamentos;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusPagamento;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagamentosRepository extends JpaRepository<Pagamentos, Long>, JpaSpecificationExecutor<Pagamentos> {

    // ==========================================
    // BUSCAS POR STATUS
    // ==========================================

    /**
     * Lista todos os pagamentos pendentes (de qualquer tipo)
     */
    List<Pagamentos> findByStatusPagamentoAndAtivoTrue(StatusPagamento statusPagamento);

    /**
     * Lista pagamentos prontos para pagamento (após validação do documento da agência)
     */
    List<Pagamentos> findByStatusPagamentoAndAtivoTrueOrderByDataVencimentoAsc(StatusPagamento statusPagamento);

    // ==========================================
    // BUSCAS POR TIPO
    // ==========================================

    /**
     * Lista pagamentos por tipo (ex: todas as comissões de agência, todas as contas diversas)
     */
    List<Pagamentos> findByTipoPagamentoAndAtivoTrue(TipoPagamento tipoPagamento);

    /**
     * Lista comissões de agência pendentes de documento (status = AGUARDANDO_DOCUMENTO)
     * Usado no portal da agência para mostrar o que falta documentar
     */
    @Query("SELECT p FROM Pagamentos p WHERE p.tipoPagamento = 'COMISSAO_AGENCIA' " +
            "AND p.statusPagamento = 'AGUARDANDO_DOCUMENTO' AND p.ativo = true")
    List<Pagamentos> buscarComissoesAgenciaAguardandoDocumento();

    /**
     * Lista comissões de agência de uma agência específica que estão aguardando documento
     * Usado quando a agência faz login no portal dela
     */
    @Query("SELECT p FROM Pagamentos p WHERE p.tipoPagamento = 'COMISSAO_AGENCIA' " +
            "AND p.agencia.agenciaId = :agenciaId " +
            "AND p.statusPagamento = 'AGUARDANDO_DOCUMENTO' AND p.ativo = true")
    List<Pagamentos> buscarComissoesAgenciaAguardandoDocumentoPorAgencia(@Param("agenciaId") Long agenciaId);

    // ==========================================
    // BUSCAS POR VÍNCULOS
    // ==========================================

    /**
     * Lista pagamentos vinculados a uma agência específica
     */
    @Query("SELECT p FROM Pagamentos p WHERE p.agencia.agenciaId = :agenciaId AND p.ativo = true " +
            "ORDER BY p.dataVencimento DESC")
    List<Pagamentos> buscarPorAgencia(@Param("agenciaId") Long agenciaId);

    /**
     * Lista pagamentos vinculados a uma compra específica
     */
    @Query("SELECT p FROM Pagamentos p WHERE p.compra.comprasId = :compraId AND p.ativo = true")
    List<Pagamentos> buscarPorCompra(@Param("compraId") Long compraId);

    /**
     * Lista pagamentos vinculados a um funcionário específico (salários, vales, comissões)
     */
    @Query("SELECT p FROM Pagamentos p WHERE p.funcionario.id = :funcionarioId AND p.ativo = true " +
            "ORDER BY p.dataVencimento DESC")
    List<Pagamentos> buscarPorFuncionario(@Param("funcionarioId") Long funcionarioId);

    /**
     * Lista pagamentos vinculados a uma parcela específica do contrato
     */
    @Query("SELECT p FROM Pagamentos p WHERE p.contratoPagamento.contratoPagamentoId = :contratoPagamentoId AND p.ativo = true")
    List<Pagamentos> buscarPorContratoPagamento(@Param("contratoPagamentoId") Long contratoPagamentoId);

    // ==========================================
    // BUSCAS POR PERÍODO (para relatórios financeiros)
    // ==========================================

    /**
     * Lista pagamentos por período de vencimento (para relatórios de contas a pagar)
     */
    @Query("SELECT p FROM Pagamentos p WHERE p.dataVencimento BETWEEN :inicio AND :fim AND p.ativo = true " +
            "ORDER BY p.dataVencimento ASC")
    List<Pagamentos> buscarPorPeriodoVencimento(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Lista pagamentos por período de pagamento efetivo (para relatórios de contas pagas)
     */
    @Query("SELECT p FROM Pagamentos p WHERE p.dataPagamento BETWEEN :inicio AND :fim AND p.ativo = true " +
            "ORDER BY p.dataPagamento DESC")
    List<Pagamentos> buscarPorPeriodoPagamento(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Lista pagamentos vencidos e ainda não pagos (para relatório de inadimplência de pagamentos)
     * Critério: dataVencimento < hoje E status em (PENDENTE, AGUARDANDO_DOCUMENTO, PRONTO_PARA_PAGAMENTO)
     */
    @Query("SELECT p FROM Pagamentos p WHERE p.dataVencimento < :hoje " +
            "AND p.statusPagamento IN :statusPendentes AND p.ativo = true " +
            "ORDER BY p.dataVencimento ASC")
    List<Pagamentos> buscarPagamentosVencidos(
            @Param("hoje") LocalDate hoje,
            @Param("statusPendentes") List<StatusPagamento> statusPendentes);

    // ==========================================
    // BUSCAS PARA PAINEL ADMINISTRATIVO / GERÊNCIA
    // ==========================================

    /**
     * Lista pagamentos de um tipo específico em um período
     * Ex: "Todas as comissões de agência pagas em Julho/2026"
     */
    @Query("SELECT p FROM Pagamentos p WHERE p.tipoPagamento = :tipo " +
            "AND p.dataPagamento BETWEEN :inicio AND :fim AND p.ativo = true")
    List<Pagamentos> buscarPorTipoEPeriodoPagamento(
            @Param("tipo") TipoPagamento tipo,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Verifica se já existe um pagamento de comissão de agência para uma parcela específica
     * Evita duplicação quando o sistema dispara comissões automaticamente
     */
    boolean existsByTipoPagamentoAndContratoPagamento_ContratoPagamentoId(
            TipoPagamento tipoPagamento,
            Long contratoPagamentoId);

    /**
     * Verifica se já existe um pagamento de compra aprovada para uma compra específica
     * Evita duplicação quando o sistema cria pagamento ao aprovar compra
     */
    boolean existsByTipoPagamentoAndCompra_ComprasId(
            TipoPagamento tipoPagamento,
            Long comprasId);

    /**
     * Lista pagamentos por beneficiário (busca por nome parcial)
     * Útil para encontrar pagamentos de um fornecedor específico
     */
    List<Pagamentos> findByBeneficiarioContainingIgnoreCaseAndAtivoTrue(String beneficiario);

    /**
     * Lista pagamentos por número de documento (para conciliação bancária)
     */
    List<Pagamentos> findByNumeroDocumentoAndAtivoTrue(String numeroDocumento);
}