package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Distribuicao {

    ROTATIVO_00_24("Rotativo das 00:00 as 24:00"),
    ROTATIVO_06_24("Rotativo das 06:00 as 24:00"),
    ROTATIVO_06_18("Rotativo das 06:00 as 18:00"),
    ROTATIVO_12_24("Rotativo das 12:00 as 24:00"),
    DETERMINADO("Deteminado"),
    PROGRAMA("Programa"),
    MADRUGADA("Madrugada"),
    MANHA("Manhã"),
    TARDE("Tarde"),
    NOITE("Noite");

    private String descricao;
}
