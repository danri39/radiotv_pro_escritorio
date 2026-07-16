package br.com.drs.radiotv_pro_escritorio.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "feriado")
public class Feriado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feriadoId;

    private String feriadoDescricao;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFeriado;
}

