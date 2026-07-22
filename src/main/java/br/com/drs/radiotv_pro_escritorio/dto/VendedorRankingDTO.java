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
public class VendedorRankingDTO {

    private Long vendedorId;
    private String vendedorNome;
    private BigDecimal metaMes;
    private BigDecimal vendasMes;
    private BigDecimal comissaoMes;
    private BigDecimal percentualAtingimento; // Ex: 120.5 (120,5% da meta)
    private Integer posicaoRanking; // 1º, 2º, 3º...
}