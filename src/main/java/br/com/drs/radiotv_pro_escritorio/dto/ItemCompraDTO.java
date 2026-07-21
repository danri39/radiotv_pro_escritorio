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
public class ItemCompraDTO {

    private Long itemCompraId;

    private String descricao;

    private Integer quantidade;

    private BigDecimal valorUnitario;

    private BigDecimal valorTotal;
}