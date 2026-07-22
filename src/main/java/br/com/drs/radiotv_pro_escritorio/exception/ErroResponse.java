package br.com.drs.radiotv_pro_escritorio.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ErroResponse(
        String mensagem,
        int status,
        String timestamp,
        String erro
) {
    public ErroResponse(String mensagem, int status, String erro) {
        this(mensagem, status, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), erro);
    }
}