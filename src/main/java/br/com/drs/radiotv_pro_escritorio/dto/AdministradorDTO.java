package br.com.drs.radiotv_pro_escritorio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorDTO {

    private String mesReferencia; // Ex: "07/2026"

    // ==========================================
    // 🏢 COMERCIAL
    // ==========================================
    private Long totalContratosAtivos;
    private Long totalContratosInadimplentes;
    private BigDecimal valorTotalContratosAtivos;
    private Long totalVendedoresAtivos;
    private Long totalAgenciasAtivas;

    // ==========================================
    // 💰 FINANCEIRO - RECEITAS
    // ==========================================
    private BigDecimal totalFaturadoMes;
    private BigDecimal totalRecebidoMes;
    private BigDecimal totalReceberAtrasado;
    private Long quantidadeParcelasEmAtraso;

    // ==========================================
    // 💸 FINANCEIRO - DESPESAS
    // ==========================================
    private BigDecimal totalFolhaPagaMes;
    private BigDecimal totalComissoesVendedoresMes;
    private BigDecimal totalComissoesAgenciasMes;
    private BigDecimal totalComprasAprovadasMes;
    private BigDecimal totalContasDiversasMes;
    private BigDecimal totalDespesasMes;

    // ==========================================
    // 🏦 BANCOS
    // ==========================================
    private String agenciaPadrao;
    private String contaCorrentePadrao;
    private String carteiraPadrao;

    // ==========================================
    // 📊 FLUXO DE CAIXA
    // ==========================================
    private BigDecimal saldoFluxoCaixa; // Recebido - Despesas

    // ==========================================
    // 👥 RH
    // ==========================================
    private Long totalFuncionariosAtivos;
    private Long totalFolhasAbertas;
    private Long totalFolhasFechadas;
    private Long totalFolhasPagas;

    // ==========================================
    // 📋 PAGAMENTOS A FAZER
    // ==========================================
    private Long totalPagamentosPendentes;
    private Long totalPagamentosAguardandoDocumento;
    private Long totalPagamentosProntosParaPagar;
    private BigDecimal valorTotalPagamentosPendentes;

    // ==========================================
    // 🛒 COMPRAS
    // ==========================================
    private Long totalComprasPendentesAprovacao;
    private Long totalComprasAprovadas;
    private Long totalComprasRecusadas;
    private Long totalComprasPagas;

    // ==========================================
    // 📈 INDICADORES GERAIS
    // ==========================================
    private Long totalComissoesVendedorPendentes;
    private Long totalBeneficiosAtivos;
}