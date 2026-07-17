package br.com.drs.radiotv_pro_escritorio.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriadoDTO {

    private Long feriadoId;

    private String feriadoDescricao;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFeriado;
}
