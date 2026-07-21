package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.Compras;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComprasRepository extends JpaRepository<Compras, Long>, JpaSpecificationExecutor<Compras> {

    List<Compras> findByFuncionario_FuncionarioIdOrderByDataCompraDesc(Long funcionarioId);


    List<Compras> findByStatusCompra(StatusCompra statusCompra);


    List<Compras> findByStatusCompraAndAtivaTrue(StatusCompra statusCompra);


    Page<Compras> findByStatusCompraAndAtivaTrue(StatusCompra statusCompra, Pageable pageable);


    boolean existsByFuncionario_FuncionarioIdAndStatusCompraIn(Long funcionarioId, List<StatusCompra> statusCompras);


    Optional<Compras> findByChaveAdministrador(String chaveAdministrador);
}