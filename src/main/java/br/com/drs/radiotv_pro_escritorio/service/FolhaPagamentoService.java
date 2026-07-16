package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.FolhaPagamentoDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.FolhaPagamentoMapper;
import br.com.drs.radiotv_pro_escritorio.model.FolhaPagamento;
import br.com.drs.radiotv_pro_escritorio.repository.FolhaPagamentoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FolhaPagamentoService {

    private final FolhaPagamentoRepository repository;
    private final FolhaPagamentoMapper mapper;

    @Transactional
    public FolhaPagamentoDTO salvar(FolhaPagamentoDTO dto) {
        FolhaPagamento entity = mapper.toEntity(dto);
        FolhaPagamento saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<FolhaPagamento> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<FolhaPagamento> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public FolhaPagamentoDTO atualizar(Long id, FolhaPagamentoDTO dto) {
        FolhaPagamento existente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, existente);
        repository.save(existente);
        return mapper.toDTO(existente);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
