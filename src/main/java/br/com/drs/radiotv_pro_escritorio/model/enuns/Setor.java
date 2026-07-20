package br.com.drs.radiotv_pro_escritorio.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Setor {

    DIRECAO("Direção Geral"),
    JORNALISMO("Jornalismo"),
    PROGRAMACAO("Programação Musical e Conteúdo"),
    PRODUCAO("Produção de Áudio e Programas"),
    LOCUCAO("Locução e Apresentação"),
    COMERCIAL("Comercial e Vendas"),
    MARKETING("Marketing e Promoções"),
    ENGENHARIA("Engenharia e Transmissão"),
    TI("Tecnologia da Informação"),
    ADMINISTRATIVO("Administrativo"),
    FINANCEIRO("Financeiro"),
    RECURSOS_HUMANOS("Recursos Humanos"),
    ZELADORIA("Zeladoria e Manutenção"),
    SEGURANCA("Segurança"),
    OUTROS("Outros");

    private final String descricao;
}
