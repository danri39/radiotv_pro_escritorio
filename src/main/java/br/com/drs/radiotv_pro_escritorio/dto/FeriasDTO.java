package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriasDTO {

    private Long feriasId;

    private Funcionario funcionario;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFim;

    private Integer quantidadeDias;

    private Integer anoReferenciaAquisitivo;

    private Boolean aprovada;

    private Boolean abonoPecuniario;

    private Integer quantidadeDiasAbono;

    private String motivoRecusa;
}
