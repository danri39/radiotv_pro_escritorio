package br.com.drs.radiotv_pro_escritorio.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RamoAtividadeDTO {

    private Long ramo_atividade_id;

    private String descricao;
}
