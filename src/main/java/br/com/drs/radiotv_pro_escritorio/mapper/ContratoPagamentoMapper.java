package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.model.ContratoPagamento;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContratoPagamentoMapper {

    ContratoPagamentoDTO toDTO(ContratoPagamento contratoPagamento);

    ContratoPagamento toEntity(ContratoPagamentoDTO dto);

    void updateEntityFromDto(ContratoPagamentoDTO dto, @MappingTarget ContratoPagamento contratoPagamento);
}
