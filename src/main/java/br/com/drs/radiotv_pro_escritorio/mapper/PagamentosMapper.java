package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.PagamentosDTO;
import br.com.drs.radiotv_pro_escritorio.model.Pagamentos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagamentosMapper {

    /**
     * Converte Entidade para DTO
     * Mapeia os dados dos relacionamentos opcionais (agência, compra, funcionário, contratoPagamento)
     */
    @Mapping(target = "agenciaId", source = "agencia.agenciaId")
    @Mapping(target = "agenciaNome", source = "agencia.nomeFantasia")
    @Mapping(target = "contratoPagamentoId", source = "contratoPagamento.contratoPagamentoId")
    @Mapping(target = "numeroParcela", source = "contratoPagamento.numeroParcela")
    @Mapping(target = "compraId", source = "compra.comprasId")
    @Mapping(target = "funcionarioId", source = "funcionario.funcionarioId")
    @Mapping(target = "funcionarioNome", source = "funcionario.nome")
    PagamentosDTO toDTO(Pagamentos entity);

    /**
     * Converte DTO para Entidade
     * Os relacionamentos são ignorados aqui e devem ser buscados no Service pelos IDs
     */
    @Mapping(target = "agencia", ignore = true)
    @Mapping(target = "contratoPagamento", ignore = true)
    @Mapping(target = "compra", ignore = true)
    @Mapping(target = "funcionario", ignore = true)
    Pagamentos toEntity(PagamentosDTO dto);

    /**
     * Converte Lista de Entidades para Lista de DTOs
     */
    List<PagamentosDTO> toDTOList(List<Pagamentos> entities);
}
