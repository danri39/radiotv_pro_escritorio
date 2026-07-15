package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoPrograma {

    MUSICAL("Musical"),
    JORNALISMO("Jornalismo"),
    ESPORTE("Esporte"),
    MISTO("Misto"),
    OUTROS("Outros");

    private String descricao;
}
