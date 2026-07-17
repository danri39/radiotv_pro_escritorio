package br.com.drs.radiotv_pro_escritorio.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ponto")
public class Ponto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pontoId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "funcionario_id", nullable = false)
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