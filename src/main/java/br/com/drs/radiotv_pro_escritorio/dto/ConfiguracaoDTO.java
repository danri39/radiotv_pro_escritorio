package br.com.drs.radiotv_pro_escritorio.dto;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfiguracaoDTO {

    private Long configuracaoId;

    private String agenciaPadrao;

    private String contaCorrentePadrao;

    private String carteiraPadrao;

    private String codigoCedentePadrao;

    private String codigoConvenioBancario;

    @Column(name = "percentual_multa_atraso")
    private Double percentualMultaAtraso; // Ex: 2.00 (%)

    private Double percentualJurosMes; // Ex: 1.00 (%) ao mês

    private Integer diasParaProtestoAutomatico; // Ex: 5 (dias após o vencimento)

    private String layoutCnabPadrao; // "CNAB240" ou "CNAB400"

    private Integer sequencialRemessaAtual; // Ex: 1 (O sistema incrementa a cada arquivo gerado: 1, 2, 3...)

    private String diretorioSalvarRemessas; // Pasta local/rede onde o sistema vai cuspir o arquivo .REM

    private String diretorioLerRetornos; // Pasta onde o usuário vai jogar o arquivo .RET do banco para o sistema ler

    private Long proximoNumeroNotaFiscal; // Guarda o sequencial das notas fiscais da rádio

    private String serieNotaFiscal; // Ex: "A", "1" ou "E"

    private Double aliquotaIssPadrao; // Alíquota de ISS do município da rádio (Ex: 2.00 ou 5.00%)

    private String emailGerenteComercial;

    private String caminhoDiretorioAudiosPlayer;

    private String pastaSalvarRoteiro;

    @Builder.Default
    private Integer toleranciaAtrasoMinutos = 5; // Tolerância em minutos (padrão: 5)

    @Builder.Default
    private Boolean permiteHorasExtras = true; // Permite horas extras?

    @Builder.Default
    private Integer maximoHorasExtrasDia = 2; // Máximo de horas extras por dia

    @Builder.Default
    private Integer intervalo6HorasMinutos = 15; // Intervalo para jornada de 6h

    @Builder.Default
    private Integer intervalo8HorasMinutos = 60; // Intervalo para jornada de 8h
}
