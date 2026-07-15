package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.ContratoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContratoPagamentoRepository extends JpaRepository<ContratoPagamento, Long> {

    Optional<ContratoPagamento> findById(Long contratoId);
}
