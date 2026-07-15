package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoDTO;
import br.com.drs.radiotv_pro_escritorio.model.Contrato;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContratoMapper {

    ContratoDTO toDTO(Contrato contrato);

    Contrato toEntity(ContratoDTO dto);

    void updateEntityFromDto(ContratoDTO dto, @MappingTarget Contrato contrato);
}
