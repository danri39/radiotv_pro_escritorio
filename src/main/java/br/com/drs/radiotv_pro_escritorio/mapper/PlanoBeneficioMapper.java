package br.com.drs.radiotv_pro_escritorio.mapper;

import br.com.drs.radiotv_pro_escritorio.dto.PlanoBeneficioDTO;
import br.com.drs.radiotv_pro_escritorio.model.PlanoBeneficio;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanoBeneficioMapper {

    /**
     * Converte Entidade para DTO
     */
    PlanoBeneficioDTO toDTO(PlanoBeneficio entity);

    /**
     * Converte DTO para Entidade (para criação)
     */
    PlanoBeneficio toEntity(PlanoBeneficioDTO dto);

    /**
     * Atualiza uma entidade existente com os dados do DTO (para edição)
     * Ignora campos null do DTO para não sobrescrever dados existentes
     */
    void updateEntityFromDto(PlanoBeneficioDTO dto, @MappingTarget PlanoBeneficio entity);

    /**
     * Converte Lista de Entidades para Lista de DTOs
     */
    List<PlanoBeneficioDTO> toDTOList(List<PlanoBeneficio> entities);
}