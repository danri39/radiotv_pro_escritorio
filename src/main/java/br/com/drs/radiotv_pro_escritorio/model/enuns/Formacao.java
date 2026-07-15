package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Formacao {

    NAOINFORMADO("Não Informado"),
    FUNDAMENTAL("Educação Fundamental"),
    MEDIO("Educação Média"),
    GRADUACAO("Graduação"),
    POSGRADUACAO("Pós Graduação"),
    MESTRADO("Mestrado"),
    DOUTORADO("Doutorado"),
    POSDOUTORADO("Pós Doutorado");

    private String descricao;
}
