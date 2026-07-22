package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.Compras;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ComprasRepository extends JpaRepository<Compras, Long>, JpaSpecificationExecutor<Compras> {

    // Busca compras de um funcionário específico, ordenadas pela data (mais recente primeiro)
    List<Compras> findByFuncionario_IdOrderByDataCompraDesc(Long funcionarioId);

    // Busca compras por status
    List<Compras> findByStatusCompra(StatusCompra statusCompra);

    // CORREÇÃO: De "AtivoTrue" para "AtivaTrue" (conforme o campo na entidade Compras)
    List<Compras> findByStatusCompraAndAtivaTrue(StatusCompra statusCompra);

    // CORREÇÃO: De "AtivoTrue" para "AtivaTrue"
    Page<Compras> findByStatusCompraAndAtivaTrue(StatusCompra statusCompra, Pageable pageable);

    // Verifica se existe compra pendente ou aprovada para um funcionário
    boolean existsByFuncionario_IdAndStatusCompraIn(Long funcionarioId, List<StatusCompra> statusCompras);

    // Busca pela chave administrativa (uso interno de segurança)
    Optional<Compras> findByChaveAdministrador(String chaveAdministrador);
}