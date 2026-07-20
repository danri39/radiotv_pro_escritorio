package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendedorDTO {

    private Long vendedorId;

    private Long funcionarioId;

    private BigDecimal metaMes;

    private String mesAno;

    private BigDecimal vendasMes;

    private BigDecimal vendasTotal;

    private int comissaoVendas;
}
