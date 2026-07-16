package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusRecebimento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecebimentosResponseDTO(
        Long recebimentosId,
        Long contratoId,
        Integer numeroParcela,
        BigDecimal valor,
        LocalDate dataVencimento,
        LocalDate dataRecebimento,
        StatusRecebimento status,
        BigDecimal comissaoVendedor,
        BigDecimal comissaoAgencia
) {}