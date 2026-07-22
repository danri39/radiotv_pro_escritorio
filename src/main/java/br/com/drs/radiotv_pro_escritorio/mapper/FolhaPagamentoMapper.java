package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.FolhaPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.model.FolhaPagamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FolhaPagamentoMapper {

    @Mapping(target = "funcionarioId", source = "funcionario.id") // CORREÇÃO: 'id' em vez de 'funcionarioId'
    @Mapping(target = "funcionarioNome", source = "funcionario.nome")
    @Mapping(target = "funcionarioCpf", source = "funcionario.cpf")
    @Mapping(target = "funcionarioCargo", source = "funcionario.cargo")
    @Mapping(target = "funcionarioVendedor", source = "funcionario.vendedor")
    FolhaPagamentoDTO toDTO(FolhaPagamento entity);

    @Mapping(target = "funcionario", ignore = true)
    FolhaPagamento toEntity(FolhaPagamentoDTO dto);

    @Mapping(target = "funcionario", ignore = true)
    @Mapping(target = "folhaPagamentoId", ignore = true)
    @Mapping(target = "statusFolha", ignore = true)
    @Mapping(target = "dataFechamento", ignore = true)
    @Mapping(target = "dataPagamento", ignore = true)
    @Mapping(target = "pagamentoId", ignore = true)
    void updateEntityFromDto(FolhaPagamentoDTO dto, @MappingTarget FolhaPagamento entity);

    List<FolhaPagamentoDTO> toDTOList(List<FolhaPagamento> entities);
}