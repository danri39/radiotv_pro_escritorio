package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.FilhosDTO;
import br.com.drs.radiotv_pro_escritorio.model.Filhos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FilhosMapper {

    FilhosDTO toDTO(Filhos filhos);

    Filhos toEntity(FilhosDTO dto);

    void UpdateEntityFromDto(FilhosDTO dto, @MappingTarget Filhos filhos);
}
