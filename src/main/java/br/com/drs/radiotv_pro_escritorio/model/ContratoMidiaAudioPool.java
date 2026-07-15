package br.com.drs.radiotv_pro_escritorio.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ContratoMidiaAudioPool {
    private String arquivo;   // Nome do arquivo .MP3 em CAIXA ALTA
    private Integer insercoes; // Quantidade de vezes que ele roda dentro do total diário
}