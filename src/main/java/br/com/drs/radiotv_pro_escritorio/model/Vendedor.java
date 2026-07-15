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
@Table(name = "vendedor")
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vendedorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    private BigDecimal metaMes;

    private String mesAno;

    private BigDecimal vendasMes;

    private BigDecimal vendasTotal;

    private int comissaoVendas;
}
