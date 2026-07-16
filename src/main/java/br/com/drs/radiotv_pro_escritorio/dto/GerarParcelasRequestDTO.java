package br.com.drs.radiotv_pro_escritorio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO para gerar as parcelas automaticamente ao criar contrato
public record GerarParcelasRequestDTO(
        Long contratoId,
        Integer quantidadeParcelas,
        BigDecimal valorTotal,
        LocalDate dataPrimeiroVencimento
) {}