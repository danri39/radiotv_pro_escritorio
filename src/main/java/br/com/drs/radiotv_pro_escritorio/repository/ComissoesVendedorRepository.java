package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.ComissoesVendedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComissoesVendedorRepository extends JpaRepository<ComissoesVendedor, Long> {
    List<ComissoesVendedor> findByVendedorVendedorIdAndPagaFalse(Long vendedorId);
    List<ComissoesVendedor> findByVendedorVendedorId(Long vendedorId);
}
