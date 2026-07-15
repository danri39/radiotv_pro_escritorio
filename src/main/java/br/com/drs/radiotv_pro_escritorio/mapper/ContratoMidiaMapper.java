package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoMidiaDTO;
import br.com.drs.radiotv_pro_escritorio.model.ContratoMidia;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContratoMidiaMapper {

    ContratoMidiaDTO toDTO(ContratoMidia contratoMidia);

    ContratoMidia toEntity(ContratoMidiaDTO dto);

    void updateEntityFromDto(ContratoMidiaDTO dto, @MappingTarget ContratoMidia contratoMidia);
}
