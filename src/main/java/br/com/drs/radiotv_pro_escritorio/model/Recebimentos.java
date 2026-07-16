package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusRecebimento;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "recebimentos")
public class Recebimentos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recebimentosId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private Contrato contrato;

    @Column(nullable = false)
    private Integer numeroParcela; // 1, 2, 3... 12

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate dataVencimento;

    private LocalDate dataRecebimento; // preenchido ao dar baixa

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusRecebimento status;

    // Comissões calculadas ao dar baixa
    @Column(precision = 10, scale = 2)
    private BigDecimal comissaoVendedor;

    @Column(precision = 10, scale = 2)
    private BigDecimal comissaoAgencia;

    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        if (this.status == null) this.status = StatusRecebimento.PENDENTE;
    }
}