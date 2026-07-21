package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusCompra;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprasDTO {

    private Long id;

    private Long funcionarioId;

    private String funcionarioNome;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataCompra;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    private StatusCompra statusCompra;

    private String justificativaRecusa;

    private Boolean ativa;

    private BigDecimal valorTotalGeral;

    private List<ItemCompraDTO> itens;
}
