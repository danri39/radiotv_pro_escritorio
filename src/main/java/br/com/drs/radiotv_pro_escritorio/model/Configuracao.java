package br.com.drs.radiotv_pro_escritorio.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "configuracao")
public class Configuracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long configuracaoId;

    private String agenciaPadrao;

    private String contaCorrentePadrao;

    private String carteiraPadrao;

    private String codigoCedentePadrao;

    private String codigoConvenioBancario;

    @Column(name = "percentual_multa_atraso")
    private Double percentualMultaAtraso; // Ex: 2.00 (%)

    @Column(name = "percentual_juros_mes")
    private Double percentualJurosMes; // Ex: 1.00 (%) ao mês

    @Column(name = "dias_para_protesto_automatico")
    private Integer diasParaProtestoAutomatico; // Ex: 5 (dias após o vencimento)

    // --- 📁 FLUXO DE ARQUIVOS CNAB (FATURAMENTO BANCO) ---
    @Column(name = "layout_cnab_padrao")
    private String layoutCnabPadrao; // "CNAB240" ou "CNAB400"

    @Column(name = "sequencial_remessa_atual")
    private Integer sequencialRemessaAtual; // Ex: 1 (O sistema incrementa a cada arquivo gerado: 1, 2, 3...)

    @Column(name = "diretorio_salvar_remessas")
    private String diretorioSalvarRemessas; // Pasta local/rede onde o sistema vai cuspir o arquivo .REM

    @Column(name = "diretorio_ler_retornos")
    private String diretorioLerRetornos; // Pasta onde o usuário vai jogar o arquivo .RET do banco para o sistema ler

    @Column(name = "proximo_numero_nota_fiscal")
    private Long proximoNumeroNotaFiscal; // Guarda o sequencial das notas fiscais da rádio

    @Column(name = "serie_nota_fiscal")
    private String serieNotaFiscal; // Ex: "A", "1" ou "E"

    @Column(name = "aliquota_iss_padrao")
    private Double aliquotaIssPadrao; // Alíquota de ISS do município da rádio (Ex: 2.00 ou 5.00%)

    private String emailGerenteComercial;

    private String caminhoDiretorioAudiosPlayer;

    private String pastaSalvarRoteiro;

    @Column(name = "tolerancia_atraso_minutos")
    @Builder.Default
    private Integer toleranciaAtrasoMinutos = 5; // Tolerância em minutos (padrão: 5)

    @Column(name = "permite_horas_extras")
    @Builder.Default
    private Boolean permiteHorasExtras = true; // Permite horas extras?

    @Column(name = "maximo_horas_extras_dia")
    @Builder.Default
    private Integer maximoHorasExtrasDia = 2; // Máximo de horas extras por dia

    @Column(name = "intervalo_6_horas_minutos")
    @Builder.Default
    private Integer intervalo6HorasMinutos = 15; // Intervalo para jornada de 6h

    @Column(name = "intervalo_8_horas_minutos")
    @Builder.Default
    private Integer intervalo8HorasMinutos = 60; // Intervalo para jornada de 8h
}
