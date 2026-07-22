package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.ItemCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemCompraRepository extends JpaRepository<ItemCompra, Long> {

    List<ItemCompra> findByCompra_ComprasId(Long compraId);

    List<ItemCompra> findByDescricaoContainingIgnoreCase(String descricao);
}