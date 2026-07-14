package br.com.drs.radiotv_pro_escritorio.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public abstract class GenericService<T, ID> {

    protected JpaRepository<T, ID> repository;

    public T salvar(T entidade) {
        return repository.save(entidade);
    }

    public List<T> listarTodos() {
        return repository.findAll();
    }

    public Optional<T> buscarPorId(ID id) {
        return repository.findById(id);
    }

    public T atualizar(T entidade) {
        return repository.save(entidade);
    }

    public void deletarPorId(ID id) {
        repository.deleteById(id);
    }
}