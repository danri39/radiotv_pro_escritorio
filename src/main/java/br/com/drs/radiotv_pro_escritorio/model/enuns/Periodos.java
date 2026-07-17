package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Periodos {

    MADRUGADA("Madrugada"),
    MANHA("Manhã"),
    TARDE("Tarde"),
    NOITE("Noite");

    private String descricao;
}