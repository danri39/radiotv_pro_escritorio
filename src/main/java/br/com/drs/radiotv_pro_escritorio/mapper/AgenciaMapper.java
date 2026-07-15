package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.AgenciaDTO;
import br.com.drs.radiotv_pro_escritorio.model.Agencia;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AgenciaMapper {

    AgenciaDTO toDTO(Agencia agencia);

    Agencia toEntity(AgenciaDTO dto);

    void updateEntityFromDto(AgenciaDTO dto, @MappingTarget Agencia agencia);
}
