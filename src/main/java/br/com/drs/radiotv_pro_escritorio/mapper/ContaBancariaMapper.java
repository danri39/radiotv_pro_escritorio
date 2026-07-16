package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.ContaBancariaDTO;
import br.com.drs.radiotv_pro_escritorio.model.ContaBancaria;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContaBancariaMapper {

    ContaBancariaDTO toDTO(ContaBancaria contas);

    ContaBancaria toEntity(ContaBancariaDTO dto);

    void updateEntityFromDto(ContaBancariaDTO dto, @MappingTarget ContaBancaria contas);
}
