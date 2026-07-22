package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCompra {

    PENDENTE("Pendente de Aprovação"),
    APROVADA("Aprovada (Aguardando Pagamento)"),
    RECUSADA("Recusada pelo Administrador"),
    PAGA("Paga e Finalizada");

    private final String descricao;
}