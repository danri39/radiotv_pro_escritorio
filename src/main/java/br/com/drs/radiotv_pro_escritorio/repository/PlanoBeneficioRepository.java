package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.PlanoBeneficio;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoBeneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoBeneficioRepository extends JpaRepository<PlanoBeneficio, Long>, JpaSpecificationExecutor<PlanoBeneficio> {

    /**
     * Lista todos os planos ativos
     */
    List<PlanoBeneficio> findByAtivoTrue();

    /**
     * Lista planos ativos por tipo de benefício
     */
    List<PlanoBeneficio> findByTipoBeneficioAndAtivoTrue(TipoBeneficio tipoBeneficio);

    /**
     * Lista planos ativos por operadora
     */
    List<PlanoBeneficio> findByOperadoraContainingIgnoreCaseAndAtivoTrue(String operadora);

    /**
     * Busca plano por nome exato (para verificar duplicidade)
     */
    Optional<PlanoBeneficio> findByNomeIgnoreCase(String nome);

    /**
     * Verifica se já existe um plano com o mesmo nome
     */
    boolean existsByNomeIgnoreCase(String nome);

    /**
     * Lista planos ativos ordenados por tipo e nome
     */
    List<PlanoBeneficio> findByAtivoTrueOrderByTipoBeneficioAscNomeAsc();
}