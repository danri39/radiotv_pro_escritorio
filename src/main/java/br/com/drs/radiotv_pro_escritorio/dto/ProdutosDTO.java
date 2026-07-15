package br.com.drs.radiotv_pro_escritorio.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdutosDTO {

    private Long produtoId;

    private String nomeProduto;

    private String descricao;

    private String marca;

    private String quantidade;

    private BigDecimal valorUnitario;

    private Integer quantidadeEstoque;
}
