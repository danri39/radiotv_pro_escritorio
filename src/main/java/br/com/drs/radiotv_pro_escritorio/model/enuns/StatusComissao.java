package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusComissao {

    PENDENTE("Pendente de Processamento na Folha"),
    PROCESSADA("Processada na Folha");

    private final String descricao;
}