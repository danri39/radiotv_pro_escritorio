package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ProdutosDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.ProdutosMapper;
import br.com.drs.radiotv_pro_escritorio.model.Produtos;
import br.com.drs.radiotv_pro_escritorio.repository.ProdutosRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutosService {

    private final ProdutosRepository repository;
    private final ProdutosMapper mapper;

    @Transactional
    public ProdutosDTO salvar(ProdutosDTO dto) {
        Produtos entity = mapper.toEntity(dto);
        Produtos saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<Produtos> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<Produtos> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ProdutosDTO atualizar(Long id, ProdutosDTO dto) {
        Produtos produtosExistente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, produtosExistente);
        repository.save(produtosExistente);
        return mapper.toDTO(produtosExistente);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
