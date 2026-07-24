package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoDTO;
import br.com.drs.radiotv_pro_escritorio.model.Contrato;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ContratoMapper {

    ContratoDTO toDTO(Contrato contrato);

    Contrato toEntity(ContratoDTO dto);

    @Mapping(target = "contratoId", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "vendedor", ignore = true)
    @Mapping(target = "agencia", ignore = true)
    void updateEntityFromDto(ContratoDTO dto, @MappingTarget Contrato contrato);
}