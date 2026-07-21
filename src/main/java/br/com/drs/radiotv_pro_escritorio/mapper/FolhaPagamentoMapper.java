package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.FolhaPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.model.FolhaPagamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FolhaPagamentoMapper {

    /**
     * Converte Entidade para DTO
     * Mapeia os dados do funcionário a partir do relacionamento
     */
    @Mapping(target = "funcionarioId", source = "funcionario.funcionarioId")
    @Mapping(target = "funcionarioNome", source = "funcionario.nome")
    @Mapping(target = "funcionarioCpf", source = "funcionario.cpf")
    @Mapping(target = "funcionarioCargo", source = "funcionario.cargo")
    @Mapping(target = "funcionarioVendedor", source = "funcionario.vendedor")
    FolhaPagamentoDTO toDTO(FolhaPagamento entity);

    /**
     * Converte DTO para Entidade (para criação)
     * O relacionamento com Funcionario é ignorado e deve ser buscado no Service
     */
    @Mapping(target = "funcionario", ignore = true)
    FolhaPagamento toEntity(FolhaPagamentoDTO dto);

    /**
     * Atualiza uma entidade existente com os dados do DTO (para edição)
     * Ignora campos que não devem ser sobrescritos
     */
    @Mapping(target = "funcionario", ignore = true)
    @Mapping(target = "folhaPagamentoId", ignore = true)
    @Mapping(target = "statusFolha", ignore = true)
    @Mapping(target = "dataFechamento", ignore = true)
    @Mapping(target = "dataPagamento", ignore = true)
    @Mapping(target = "pagamentoId", ignore = true)
    void updateEntityFromDto(FolhaPagamentoDTO dto, @MappingTarget FolhaPagamento entity);

    /**
     * Converte Lista de Entidades para Lista de DTOs
     */
    List<FolhaPagamentoDTO> toDTOList(List<FolhaPagamento> entities);
}