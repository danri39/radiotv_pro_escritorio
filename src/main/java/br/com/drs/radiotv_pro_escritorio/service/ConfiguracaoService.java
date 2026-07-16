package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ConfiguracaoDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.ConfiguracaoMapper;
import br.com.drs.radiotv_pro_escritorio.model.Configuracao;
import br.com.drs.radiotv_pro_escritorio.repository.ConfiguracaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfiguracaoService {

    private final ConfiguracaoRepository repository;
    private final ConfiguracaoMapper mapper;

    @Transactional
    public ConfiguracaoDTO salvar(ConfiguracaoDTO dto) {
        Configuracao entity = mapper.toEntity(dto);
        Configuracao saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<Configuracao> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<Configuracao> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ConfiguracaoDTO atualizar(Long id, ConfiguracaoDTO dto) {
        Configuracao existente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Configuração não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, existente);
        repository.save(existente);
        return mapper.toDTO(existente);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}