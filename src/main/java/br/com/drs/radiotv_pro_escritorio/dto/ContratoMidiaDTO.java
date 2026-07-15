package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.Contrato;
import br.com.drs.radiotv_pro_escritorio.model.ContratoMidiaAudioPool;
import br.com.drs.radiotv_pro_escritorio.model.Programa;
import br.com.drs.radiotv_pro_escritorio.model.RamoAtividade;
import br.com.drs.radiotv_pro_escritorio.model.enuns.DiasSemana;
import br.com.drs.radiotv_pro_escritorio.model.enuns.Distribuicao;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TempoMidia;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoMidia;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContratoMidiaDTO {

    private Long contratoMidiaId;

    private Contrato contrato;

    private TipoMidia tipoMidia;

    private String identificacao;

    private int quantidade;

    private TempoMidia tempoMidia;

    private List<DiasSemana> diasSemana;

    private Distribuicao distribuicao;

    private LocalTime horarioEspecifico;

    private Programa programa;

    private RamoAtividade ramoAtividade;

    private Integer prioridade;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFinal;

    private Boolean ativo;

    private Boolean temMultiplosAudios = false;

    private List<ContratoMidiaAudioPool> audiosPool;
}
