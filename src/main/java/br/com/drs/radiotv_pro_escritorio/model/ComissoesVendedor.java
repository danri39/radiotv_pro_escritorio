package br.com.drs.radiotv_pro_escritorio.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comissoes_vendedor")
public class ComissoesVendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comissoesVendedorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Vendedor vendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recebimentos_id")
    private Recebimentos recebimentos; // origem da comissão

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate dataCalculo;

    @Column(nullable = false)
    private Boolean paga; // false = acumulada, true = já foi para folha
}