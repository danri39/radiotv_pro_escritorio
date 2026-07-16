package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPagamento;

import java.math.BigDecimal;

public record PagamentosRequestDTO(

        Long comprasId,

        TipoPagamento tipo,

        BigDecimal valor,

        String descricao
) {}
