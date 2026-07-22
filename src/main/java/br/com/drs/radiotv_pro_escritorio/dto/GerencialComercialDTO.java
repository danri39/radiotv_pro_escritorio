package br.com.drs.radiotv_pro_escritorio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GerencialComercialDTO {

    private String mesReferencia;

    // ==========================================
    // 📊 CONTRATOS
    // ==========================================
    private Long totalContratosAtivos;
    private Long totalContratosEncerrados;
    private Long totalContratosInadimplentes;
    private BigDecimal valorTotalContratosAtivos;
    private BigDecimal valorTotalReceber;

    // ==========================================
    // 👥 VENDEDORES
    // ==========================================
    private Long totalVendedoresAtivos;
    private BigDecimal totalVendasMes;
    private BigDecimal totalComissoesVendedoresMes;

    // Ranking de vendedores (top performers)
    private List<VendedorRankingDTO> rankingVendedores;

    // ==========================================
    // 🏢 AGÊNCIAS
    // ==========================================
    private Long totalAgenciasAtivas;
    private BigDecimal totalVendasAgenciasMes;
    private BigDecimal totalComissoesAgenciasMes;

    // ==========================================
    // 📈 METAS
    // ==========================================
    private BigDecimal metaVendasMes;
    private BigDecimal vendasRealizadasMes;
    private BigDecimal percentualAtingimentoMeta; // Ex: 85.5 (85,5% da meta)

    // ==========================================
    // ⚠️ ALERTAS COMERCIAIS
    // ==========================================
    private Long contratosVencendoProximoMes;
    private Long vendedoresAbaixoDaMeta;
}