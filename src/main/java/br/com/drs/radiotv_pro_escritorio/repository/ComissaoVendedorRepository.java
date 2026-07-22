package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.ComissaoVendedor;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusComissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ComissaoVendedorRepository extends JpaRepository<ComissaoVendedor, Long>, JpaSpecificationExecutor<ComissaoVendedor> {

    // ==========================================
    // BUSCAS POR VENDEDOR
    // ==========================================

    /**
     * Lista todas as comissões de um vendedor específico
     */
    @Query("SELECT c FROM ComissaoVendedor c WHERE c.vendedor.vendedorId = :vendedorId AND c.ativa = true " +
            "ORDER BY c.dataCalculo DESC")
    List<ComissaoVendedor> buscarPorVendedor(@Param("vendedorId") Long vendedorId);

    /**
     * Lista comissões de um vendedor por status
     */
    @Query("SELECT c FROM ComissaoVendedor c WHERE c.vendedor.vendedorId = :vendedorId " +
            "AND c.statusComissao = :status AND c.ativa = true ORDER BY c.dataCalculo DESC")
    List<ComissaoVendedor> buscarPorVendedorEStatus(
            @Param("vendedorId") Long vendedorId,
            @Param("status") StatusComissao status);

    // ==========================================
    // BUSCAS PARA PROCESSAMENTO NA FOLHA DE PAGAMENTO
    // ==========================================

    /**
     * Lista todas as comissões PENDENTES de um vendedor em um mês de referência específico.
     * Usado pelo FolhaPagamentoService para fechar a folha.
     */
    @Query("SELECT c FROM ComissaoVendedor c WHERE c.vendedor.vendedorId = :vendedorId " +
            "AND c.mesReferencia = :mesReferencia AND c.statusComissao = 'PENDENTE' AND c.ativa = true")
    List<ComissaoVendedor> buscarPendentesPorVendedorEMes(
            @Param("vendedorId") Long vendedorId,
            @Param("mesReferencia") String mesReferencia);

    /**
     * Lista todas as comissões PENDENTES de um mês de referência (de todos os vendedores).
     * Usado para relatórios administrativos.
     */
    @Query("SELECT c FROM ComissaoVendedor c WHERE c.mesReferencia = :mesReferencia " +
            "AND c.statusComissao = 'PENDENTE' AND c.ativa = true")
    List<ComissaoVendedor> buscarPendentesPorMes(@Param("mesReferencia") String mesReferencia);

    /**
     * Soma o valor total das comissões pendentes de um vendedor em um mês.
     * Usado para calcular o total a pagar na folha.
     */
    @Query("SELECT COALESCE(SUM(c.valorComissao), 0) FROM ComissaoVendedor c " +
            "WHERE c.vendedor.vendedorId = :vendedorId AND c.mesReferencia = :mesReferencia " +
            "AND c.statusComissao = 'PENDENTE' AND c.ativa = true")
    BigDecimal somarComissoesPendentesPorVendedorEMes(
            @Param("vendedorId") Long vendedorId,
            @Param("mesReferencia") String mesReferencia);

    // ==========================================
    // BUSCAS POR CONTRATO/PARCELA (controle de duplicação)
    // ==========================================

    /**
     * Verifica se já existe comissão lançada para uma parcela específica.
     * Evita duplicação quando o sistema dispara comissões automaticamente.
     */
    boolean existsByContratoPagamento_ContratoPagamentoIdAndAtivaTrue(Long contratoPagamentoId);

    /**
     * Busca a comissão de uma parcela específica (se existir).
     */
    Optional<ComissaoVendedor> findByContratoPagamento_ContratoPagamentoIdAndAtivaTrue(Long contratoPagamentoId);

    /**
     * Lista comissões de um contrato específico (todas as parcelas).
     */
    @Query("SELECT c FROM ComissaoVendedor c WHERE c.contratoPagamento.contrato.contratoId = :contratoId " +
            "AND c.ativa = true ORDER BY c.contratoPagamento.numeroParcela ASC")
    List<ComissaoVendedor> buscarPorContrato(@Param("contratoId") Long contratoId);

    // ==========================================
    // BUSCAS POR PERÍODO (relatórios)
    // ==========================================

    /**
     * Lista comissões por período de cálculo (quando o cliente pagou).
     */
    @Query("SELECT c FROM ComissaoVendedor c WHERE c.dataCalculo BETWEEN :inicio AND :fim AND c.ativa = true " +
            "ORDER BY c.dataCalculo DESC")
    List<ComissaoVendedor> buscarPorPeriodoCalculo(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Lista comissões por período de processamento na folha.
     */
    @Query("SELECT c FROM ComissaoVendedor c WHERE c.dataProcessamento BETWEEN :inicio AND :fim AND c.ativa = true " +
            "ORDER BY c.dataProcessamento DESC")
    List<ComissaoVendedor> buscarPorPeriodoProcessamento(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    // ==========================================
    // BUSCAS POR STATUS
    // ==========================================

    /**
     * Lista todas as comissões com um status específico.
     */
    List<ComissaoVendedor> findByStatusComissaoAndAtivaTrue(StatusComissao statusComissao);

    /**
     * Lista comissões processadas em uma folha específica.
     */
    @Query("SELECT c FROM ComissaoVendedor c WHERE c.folhaPagamentoId = :folhaId AND c.ativa = true")
    List<ComissaoVendedor> buscarPorFolhaPagamento(@Param("folhaId") Long folhaId);

    // ==========================================
    // CONTAGENS PARA DASHBOARD
    // ==========================================

    /**
     * Conta comissões pendentes de um vendedor.
     */
    long countByVendedor_VendedorIdAndStatusComissaoAndAtivaTrue(Long vendedorId, StatusComissao status);

    /**
     * Conta comissões pendentes no total (todos os vendedores).
     */
    long countByStatusComissaoAndAtivaTrue(StatusComissao status);
}