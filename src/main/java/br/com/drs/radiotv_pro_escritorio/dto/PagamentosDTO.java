package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusPagamento;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentosDTO(

        Long pagamentosId,

        Long comprasId,

        TipoPagamento tipo,

        BigDecimal valor,

        String descricao,

        StatusPagamento status,

        LocalDateTime dataCriacao,

        LocalDateTime dataPagamento
) {}
