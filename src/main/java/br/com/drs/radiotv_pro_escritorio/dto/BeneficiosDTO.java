package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoBeneficiario;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiosDTO {

    private Long BeneficiosId;

    private Funcionario funcionario;

    private String nomeBeneficio; // Ex: "Plano de Saúde Unimed", "Odontológico"

    private TipoBeneficiario tipoBeneficiario; // TITULAR, FILHO, etc.

    private String nomeDependente; // Preenchido se for dependente (Ex: "João Silva Filho")

    private BigDecimal mensalidade; // Valor fixo mensal desta pessoa específica

    private BigDecimal coparticipacao; // Valor variável do mês (consultas, exames)
}
