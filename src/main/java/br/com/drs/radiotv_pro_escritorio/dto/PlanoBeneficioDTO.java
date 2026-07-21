package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoBeneficio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanoBeneficioDTO {

    private Long planoBeneficioId;

    // Nome do plano (ex: "Plano de Saúde Unimed - Titular")
    private String nome;

    // Tipo de benefício (PLANO_SAUDE, PLANO_ODONTOLOGICO, etc.)
    private TipoBeneficio tipoBeneficio;

    // Valor fixo mensal do plano
    private BigDecimal valorMensalFixo;

    // Descrição detalhada do plano (opcional)
    private String descricao;

    // Operadora do plano (ex: "Unimed", "Bradesco Saúde")
    private String operadora;

    // Se o plano está ativo para novos vínculos
    private Boolean ativo;
}