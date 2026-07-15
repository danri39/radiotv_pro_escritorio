package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoBeneficiario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "beneficios")
public class Beneficios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long BeneficiosId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(nullable = false)
    private String nomeBeneficio; // Ex: "Plano de Saúde Unimed", "Odontológico"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBeneficiario tipoBeneficiario; // TITULAR, FILHO, etc.

    private String nomeDependente; // Preenchido se for dependente (Ex: "João Silva Filho")

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal mensalidade; // Valor fixo mensal desta pessoa específica

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal coparticipacao; // Valor variável do mês (consultas, exames)
}
