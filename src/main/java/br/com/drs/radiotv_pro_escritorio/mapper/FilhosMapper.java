package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.FilhosDTO;
import br.com.drs.radiotv_pro_escritorio.model.Filhos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FilhosMapper {

    @Mapping(source = "funcionario.id", target = "id")
    @Mapping(source = "funcionario.nome", target = "nomeFuncionario")
    FilhosDTO toDTO(Filhos filhos);

    @Mapping(target = "funcionario", ignore = true)
    Filhos toEntity(FilhosDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "funcionario", ignore = true)
    void updateEntityFromDto(FilhosDTO dto, @MappingTarget Filhos filhos);
}