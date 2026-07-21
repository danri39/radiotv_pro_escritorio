package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusCompra;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "compras")
public class Compras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comprasId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataCompra;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ItemCompra> itens = new ArrayList<>();

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusCompra statusCompra = StatusCompra.PENDENTE;

    private String justificativaRecusa;

    @JsonIgnore
    @Column(length = 36)
    private String chaveAdministrador;

    @Builder.Default
    private Boolean ativa = true;

    @Column(name = "valor_total_geral", precision = 10, scale = 2)
    private BigDecimal valorTotalGeral;

    // Métodos helper
    public void addItem(ItemCompra item) {
        itens.add(item);
        item.setCompra(this);
    }

    public void removeItem(ItemCompra item) {
        itens.remove(item);
        item.setCompra(null);
    }

    // Método útil para o Service: invalida a chave se a compra for editada após aprovação
    public void invalidarAprovacao() {
        this.chaveAdministrador = null;
        this.statusCompra = StatusCompra.PENDENTE;
        this.justificativaRecusa = null;
    }
}