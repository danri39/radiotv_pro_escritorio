package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.Pagamentos;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagamentosRepository extends JpaRepository<Pagamentos, Long> {

    List<Pagamentos> findByStatus(StatusPagamento status);

    List<Pagamentos> findByComprasComprasId(Long comprasId);
}