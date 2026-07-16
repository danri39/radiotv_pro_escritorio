package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.RecebimentosRequestDTO;
import br.com.drs.radiotv_pro_escritorio.dto.RecebimentosResponseDTO;
import br.com.drs.radiotv_pro_escritorio.model.Recebimentos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecebimentosMapper {

    @Mapping(target = "contratoId", source = "contrato.contratoId")
    RecebimentosResponseDTO toDTO(Recebimentos entity);

    @Mapping(target = "contrato", ignore = true)
    @Mapping(target = "recebimentosId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataRecebimento", ignore = true)
    @Mapping(target = "comissaoVendedor", ignore = true)
    @Mapping(target = "comissaoAgencia", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    Recebimentos toEntity(RecebimentosRequestDTO dto);
}