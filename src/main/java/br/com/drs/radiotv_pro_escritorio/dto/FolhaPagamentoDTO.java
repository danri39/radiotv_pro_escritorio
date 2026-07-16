package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolhaPagamentoDTO {

    private Long folhaPagamentoId;

    private Funcionario funcionario;

    private String mesAno;

    private BigDecimal salarioBruto;

    private BigDecimal comissao;

    private BigDecimal outrosPagamentos;

    private BigDecimal descontoInss;

    private BigDecimal descontoIrrf;

    private BigDecimal descontoBeneficios; // Soma de (Mensalidade + Coparticipação) de todos os dependentes e titular

    private BigDecimal totalDescontos;

    private BigDecimal salarioLiquido;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    @Builder.Default
    private Boolean fechada = false;
}
