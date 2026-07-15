package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoMidia {

    GRAVADO("Gravado"),
    AO_VIVO("Ao Vivo"),
    TESTEMUNHAL("Testemunhal"),
    FLASH_TELEFONE("Flash Telefone"),
    FLASH_VIATURA("Flash Viatura"),
    FLASH_VIATURA_PROMOTERS("Flash Viatura com Promoters"),
    PATROCINIO_PROGRAMA("Patrocínio Programa"),
    PATROCINIO_QUADRO("Patricínio Quadro"),
    PATROCINIO_HORA("Patrocínio Hora"),
    PATROCINIO_TEMPERATURA("Patrocínio Temperatura"),
    PATROCINIO_DATAS_ESPECIAIS("Patrocínio Datas Especiais"),
    PATROCINIO_MEIA_HORA("Patrocínio Meia Hora"),
    PATROCINIO_CHAMADA("Patrocínio de Chamadas"),
    PATROCINIO_HORARIO("Patrocínio Horário");

    private String descricao;
}
