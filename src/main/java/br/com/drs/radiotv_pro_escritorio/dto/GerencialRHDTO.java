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
public class GerencialRHDTO {

    private String mesReferencia;

    // ==========================================
    // 👥 FUNCIONÁRIOS
    // ==========================================
    private Long totalFuncionariosAtivos;
    private Long totalFuncionariosVendedores;
    private Long totalFuncionariosAdministrativos;
    private BigDecimal totalFolhaSalarialMes; // Soma dos salários brutos

    // ==========================================
    // 📝 FOLHA DE PAGAMENTO
    // ==========================================
    private Long totalFolhasAbertas;
    private Long totalFolhasFechadas;
    private Long totalFolhasPagas;
    private BigDecimal totalLiquidoPagoMes;
    private BigDecimal totalComissoesPagasMes;
    private BigDecimal totalBeneficiosDescontadosMes;

    // ==========================================
    // 🏥 BENEFÍCIOS
    // ==========================================
    private Long totalBeneficiosAtivos;
    private BigDecimal totalValorBeneficiosMes;
    private BigDecimal totalCoparticipacoesMes;

    // ==========================================
    // 🤝 COMISSÕES
    // ==========================================
    private Long totalComissoesVendedorPendentes;
    private Long totalComissoesVendedorProcessadas;
    private BigDecimal valorTotalComissoesPendentes;

    // ==========================================
    // 📊 INDICADORES DE RH
    // ==========================================
    private BigDecimal custoMedioPorFuncionario; // Folha total / total funcionários
    private BigDecimal percentualComissoesSobreFolha; // Comissões / Folha total * 100

    // ==========================================
    // ⚠️ ALERTAS DE RH
    // ==========================================
    private Long funcionariosSemFolhaNoMes;
    private Long funcionariosComComissaoPendente;
}