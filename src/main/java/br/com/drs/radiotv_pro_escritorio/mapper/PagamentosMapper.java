package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.PagamentosDTO;
import br.com.drs.radiotv_pro_escritorio.dto.PagamentosRequestDTO;
import br.com.drs.radiotv_pro_escritorio.model.Pagamentos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PagamentosMapper {

    @Mapping(target = "comprasId", source = "compras.comprasId")
    PagamentosDTO toDTO(Pagamentos pagamentos);

    @Mapping(target = "compras", ignore = true) // Será setado manualmente no service
    @Mapping(target = "pagamentosId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataPagamento", ignore = true)
    Pagamentos toEntity(PagamentosRequestDTO dto);

    @Mapping(target = "compras", ignore = true)
    @Mapping(target = "pagamentosId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataPagamento", ignore = true)
    void updateEntityFromDto(PagamentosRequestDTO dto, @MappingTarget Pagamentos pagamentos);
}