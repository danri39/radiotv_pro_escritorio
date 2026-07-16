package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.Recebimentos;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusRecebimento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecebimentosRepository extends JpaRepository<Recebimentos, Long> {
    List<Recebimentos> findByContratoContratoId(Long contratoId);
    List<Recebimentos> findByStatus(StatusRecebimento status);
}