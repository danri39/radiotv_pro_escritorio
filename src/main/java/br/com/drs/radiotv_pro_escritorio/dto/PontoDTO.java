package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.Funcionario;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PontoDTO {

    private Long pontoId;

    private Funcionario funcionario;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate data;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime horaEntrada;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime horaSaidaIntervalo;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime horaEntradaIntervalo;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime horaSaida;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime horaEntradaExtra;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime horaSaidaExtra;

    private Boolean precisaAcerto;

    private String motivoAcerto;

    private Integer horasNormalDia;

    private Integer horasExtraDia;

    private Integer horasExtrasMes;
}
