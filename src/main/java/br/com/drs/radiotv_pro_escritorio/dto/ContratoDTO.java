package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.Agencia;
import br.com.drs.radiotv_pro_escritorio.model.Cliente;
import br.com.drs.radiotv_pro_escritorio.model.Vendedor;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContratoDTO {

    private Long contratoId;

    private Cliente cliente;

    private Vendedor vendedor;

    private Agencia agencia;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFinal;

    private BigDecimal valorTotal;

    private Integer quantidadeParcelas;

    private BigDecimal valorParcelas;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPrimeiroPagamento;

    @Builder.Default
    private Boolean contratoBonificado = false;

    @Builder.Default
    private Boolean ativo = true;
}
