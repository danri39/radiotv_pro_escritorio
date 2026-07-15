package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.Contrato;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoPagamentoDTO {

    private Long ContratoPagamentoId;

    @ManyToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    private BigDecimal valorParcela;

    private Boolean faturado;

    private Boolean paga;

    private LocalDate dataPagamentoReal;

    private BigDecimal valorEfetivoPago;

    private String numeroFatura;

    private Boolean ativo;
}
