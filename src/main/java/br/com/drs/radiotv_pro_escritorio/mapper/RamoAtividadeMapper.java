package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.RamoAtividadeDTO;
import br.com.drs.radiotv_pro_escritorio.model.RamoAtividade;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RamoAtividadeMapper {

    RamoAtividadeDTO toDTO(RamoAtividade ramoAtividade);

    RamoAtividade toEntity(RamoAtividadeDTO dto);

    void updateEntityFromDto(RamoAtividadeDTO dto, @MappingTarget RamoAtividade ramoAtividade);
}
