package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.DiasSemana;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Periodos;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HorariosBreaksDTO {

    private Long horariosBreaksId;

    private List<Periodos> periodos;

    private List<DiasSemana> diasSemana;

    private Integer breaksPorHora; // ex: 4 (significa breaks de 15 em 15 minutos)

    private Integer tempoBreaks; // ex: 3 (tempo de duração do bloco em minutos)
}
