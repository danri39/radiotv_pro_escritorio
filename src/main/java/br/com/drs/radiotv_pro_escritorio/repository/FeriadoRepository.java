package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.Feriado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeriadoRepository extends JpaRepository<Feriado, Long> {
}
