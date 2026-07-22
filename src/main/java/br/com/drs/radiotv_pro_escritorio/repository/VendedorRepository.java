package br.com.drs.radiotv_pro_escritorio.repository;

import br.com.drs.radiotv_pro_escritorio.model.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VendedorRepository extends JpaRepository<Vendedor, Long> {

    // CORREÇÃO: O 'ativo' está no Funcionario, não no Vendedor diretamente
    Long countByFuncionario_AtivoTrue();

    List<Vendedor> findByFuncionario_AtivoTrue();

    // Buscar vendedor pelo ID do funcionário
    Optional<Vendedor> findByFuncionario_Id(Long funcionarioId);
}