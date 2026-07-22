package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.ComprasDTO;
import br.com.drs.radiotv_pro_escritorio.model.Compras;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemCompraMapper.class})
public interface ComprasMapper {

    // CORREÇÃO AQUI: de "funcionario.id" para "funcionario.id"
    @Mapping(target = "funcionarioId", source = "funcionario.id")
    @Mapping(target = "funcionarioNome", source = "funcionario.nome")
    ComprasDTO toDTO(Compras entity);

    @Mapping(target = "funcionario", ignore = true)
    @Mapping(target = "chaveAdministrador", ignore = true) // Segurança extra no mapper
    Compras toEntity(ComprasDTO dto);

    List<ComprasDTO> toDTOList(List<Compras> entities);
}