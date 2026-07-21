package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.LancamentoBeneficioMensalDTO;
import br.com.drs.radiotv_pro_escritorio.model.LancamentoBeneficioMensal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LancamentoBeneficioMensalMapper {

    /**
     * Converte Entidade para DTO
     * Mapeia os dados dos relacionamentos aninhados:
     * - funcionario → funcionarioId, nome, cpf
     * - planoBeneficio → planoBeneficioId, nome, operadora, valorMensalFixo
     * - calcula o valorTotalMensal automaticamente
     */
    @Mapping(target = "funcionarioId", source = "funcionario.funcionarioId")
    @Mapping(target = "funcionarioNome", source = "funcionario.nome")
    @Mapping(target = "funcionarioCpf", source = "funcionario.cpf")

    @Mapping(target = "planoBeneficioId", source = "planoBeneficio.planoBeneficioId")
    @Mapping(target = "planoBeneficioNome", source = "planoBeneficio.nome")
    @Mapping(target = "operadora", source = "planoBeneficio.operadora")
    @Mapping(target = "valorMensalFixo", source = "planoBeneficio.valorMensalFixo")

    @Mapping(target = "valorTotalMensal", source = ".", qualifiedByName = "calcularTotalMensal")
    LancamentoBeneficioMensalDTO toDTO(LancamentoBeneficioMensal entity);

    /**
     * Converte DTO para Entidade
     * Os relacionamentos são ignorados aqui e devem ser buscados no Service pelos IDs
     */
    @Mapping(target = "funcionario", ignore = true)
    @Mapping(target = "planoBeneficio", ignore = true)
    LancamentoBeneficioMensal toEntity(LancamentoBeneficioMensalDTO dto);

    /**
     * Converte Lista de Entidades para Lista de DTOs
     */
    List<LancamentoBeneficioMensalDTO> toDTOList(List<LancamentoBeneficioMensal> entities);

    /**
     * Método auxiliar para calcular o valor total mensal no DTO
     */
    @Named("calcularTotalMensal")
    default java.math.BigDecimal calcularTotalMensal(LancamentoBeneficioMensal entity) {
        return entity != null ? entity.calcularTotalMensal() : java.math.BigDecimal.ZERO;
    }
}