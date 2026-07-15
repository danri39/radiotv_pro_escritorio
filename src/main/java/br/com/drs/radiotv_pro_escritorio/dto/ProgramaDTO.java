package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoMusica;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPrograma;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgramaDTO {

    private Long programaId;

    private String nomePrograma;

    private LocalTime horaInicio;

    private LocalTime horaFinal;

    private Boolean feriados;

    private Boolean breaks;

    private Boolean breaksProprios;

    private TipoPrograma tipoPrograma;

    private TipoMusica tipoMusica;

    @Builder.Default
    private Boolean nacional = false;

    @Builder.Default
    private Boolean ativo = true;
}
