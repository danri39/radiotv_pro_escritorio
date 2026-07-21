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
@Table(name = "plano_beneficio")
public class PlanoBeneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planoBeneficioId;

    @Column(nullable = false)
    private String nome; // Ex: "Plano de Saúde Unimed", "Odontológico Sul"

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMensalFixo; // O valor base do plano

    @Builder.Default
    private Boolean ativo = true;
}