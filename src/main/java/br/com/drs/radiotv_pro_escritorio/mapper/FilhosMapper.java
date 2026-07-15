package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.FilhosDTO;
import br.com.drs.radiotv_pro_escritorio.model.Filhos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FilhosMapper {

    @Mapping(source = "funcionario.funcionarioId", target = "funcionarioId")
    FilhosDTO toDTO(Filhos filhos);

    @Mapping(target = "funcionarioId", ignore = true)
    Filhos toEntity(FilhosDTO dto);

    @Mapping(target = "funcionarioId", ignore = true)
    void UpdateEntityFromDto(FilhosDTO dto, @MappingTarget Filhos filhos);
}
