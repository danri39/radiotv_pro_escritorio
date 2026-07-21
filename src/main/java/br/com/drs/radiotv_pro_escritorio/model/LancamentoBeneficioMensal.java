package br.com.drs.radiotv_pro_escritorio.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lancamento_beneficio_mensal")
public class LancamentoBeneficioMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lancamentoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plano_beneficio_id", nullable = false)
    private PlanoBeneficio planoBeneficio; // Puxa o valor fixo daqui

    @Column(nullable = false, length = 7)
    private String mesReferencia; // Ex: "07/2026"

    @Column(precision = 10, scale = 2)
    private BigDecimal valorCoparticipacao; // Valor variável deste mês (Exames, consultas)-
}