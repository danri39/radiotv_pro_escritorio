package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.HorariosBreaksDTO;
import br.com.drs.radiotv_pro_escritorio.model.HorariosBreaks;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HorariosBreaksMapper {

    HorariosBreaksDTO toDTO(HorariosBreaks horariosBreaks);

    HorariosBreaks toEntity(HorariosBreaksDTO dto);

    void updateEntityFromDto(HorariosBreaksDTO dto, @MappingTarget HorariosBreaks horariosBreaks);
}
