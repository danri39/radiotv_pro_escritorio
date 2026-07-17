package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.Filhos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FilhosRepository extends JpaRepository<Filhos, Long> {

    @Query("SELECT f FROM Filhos f JOIN FETCH f.funcionario WHERE f.id = :id")
    Optional<Filhos> findByIdWithFuncionario(@Param("id") Long id);

    @Query("SELECT f FROM Filhos f JOIN FETCH f.funcionario")
    List<Filhos> findAllWithFuncionario();
}