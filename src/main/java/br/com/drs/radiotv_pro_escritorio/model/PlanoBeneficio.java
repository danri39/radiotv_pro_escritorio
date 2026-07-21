package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoBeneficio;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "plano_beneficio")
public class PlanoBeneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planoBeneficioId;

    // Nome do plano (ex: "Plano de Saúde Unimed", "Odontológico SulAmérica")
    @Column(nullable = false, length = 150)
    private String nome;

    // Tipo de benefício (SAUDE, ODONTOLOGICO, SEGURO_VIDA, etc.)
    // Vamos criar um Enum TipoBeneficio para isso
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBeneficio tipoBeneficio;

    // Valor fixo mensal do plano (ex: R$ 300,00 para titular, R$ 150,00 para dependente)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMensalFixo;

    // Descrição detalhada do plano (opcional)
    @Column(length = 500)
    private String descricao;

    // Operadora do plano (ex: "Unimed", "Bradesco Saúde")
    private String operadora;

    // Se o plano está ativo para novos vínculos
    @Builder.Default
    private Boolean ativo = true;
}