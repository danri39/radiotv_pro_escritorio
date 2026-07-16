package br.com.drs.radiotv_pro_escritorio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecebimentosRequestDTO(
        Long contratoId,
        Integer numeroParcela,
        BigDecimal valor,
        LocalDate dataVencimento
) {}
