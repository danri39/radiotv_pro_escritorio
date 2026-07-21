package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    Long countByAtivoTrue();

    Collection<Object> findByAtivoTrue();
}
