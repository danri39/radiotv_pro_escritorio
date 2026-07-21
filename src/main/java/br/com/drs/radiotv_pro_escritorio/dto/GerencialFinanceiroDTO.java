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
public class GerencialFinanceiroDTO {

    private String mesReferencia;

    // ==========================================
    // 💰 RECEITAS
    // ==========================================
    private BigDecimal totalFaturadoMes;
    private BigDecimal totalRecebidoMes;
    private BigDecimal totalReceberAtrasado;
    private Long quantidadeParcelasEmAtraso;
    private Long quantidadeParcelasAReceber;

    // ==========================================
    // 💸 DESPESAS
    // ==========================================
    private BigDecimal totalFolhaPagaMes;
    private BigDecimal totalComissoesVendedoresMes;
    private BigDecimal totalComissoesAgenciasMes;
    private BigDecimal totalComprasAprovadasMes;
    private BigDecimal totalContasDiversasMes;
    private BigDecimal totalDespesasMes;

    // ==========================================
    // 📊 FLUXO DE CAIXA
    // ==========================================
    private BigDecimal saldoFluxoCaixa; // Recebido - Despesas
    private BigDecimal saldoPrevisaoMes; // Previsto a receber - Previsto a pagar

    // ==========================================
    // 🏦 BANCOS
    // ==========================================
    private String agenciaPadrao;
    private String contaCorrentePadrao;
    private String carteiraPadrao;

    // ==========================================
    // 📋 CONTAS A PAGAR
    // ==========================================
    private Long totalPagamentosPendentes;
    private Long totalPagamentosAguardandoDocumento;
    private Long totalPagamentosProntosParaPagar;
    private BigDecimal valorTotalPagamentosPendentes;

    // ==========================================
    // 🛒 COMPRAS
    // ==========================================
    private Long totalComprasPendentesAprovacao;
    private Long totalComprasAprovadasPendentesPagamento;
    private BigDecimal valorComprasPendentes;

    // ==========================================
    // ⚠️ INDICADORES FINANCEIROS
    // ==========================================
    private BigDecimal margemOperacional; // (Receita - Despesa) / Receita * 100
    private BigDecimal indiceInadimplencia; // Atrasados / Faturado * 100
}