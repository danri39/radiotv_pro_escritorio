package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.ProgramaDTO;
import br.com.drs.radiotv_pro_escritorio.model.Programa;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProgramaMapper {

    ProgramaDTO toDTO(Programa programa);

    Programa toEntity(ProgramaDTO dto);

    void updateEntityFromDto(ProgramaDTO dto, @MappingTarget Programa programa);
}
