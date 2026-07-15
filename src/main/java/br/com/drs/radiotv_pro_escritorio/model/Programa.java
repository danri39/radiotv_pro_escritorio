package br.com.drs.radiotv_pro_escritorio.model;

import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoMusica;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPrograma;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "programa")
public class Programa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long programaId;

    private String nomePrograma;

    private LocalTime horaInicio;

    private LocalTime horaFinal;

    private Boolean feriados;

    private Boolean breaks;

    private Boolean breaksProprios;

    @Enumerated(EnumType.STRING)
    private TipoPrograma tipoPrograma;

    @Enumerated(EnumType.STRING)
    private TipoMusica tipoMusica;

    @Builder.Default
    private Boolean nacional = false;

    @Builder.Default
    private Boolean ativo = true;
}
