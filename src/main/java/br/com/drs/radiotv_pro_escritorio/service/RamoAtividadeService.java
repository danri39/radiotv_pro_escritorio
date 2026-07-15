package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.RamoAtividadeDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.RamoAtividadeMapper;
import br.com.drs.radiotv_pro_escritorio.model.RamoAtividade;
import br.com.drs.radiotv_pro_escritorio.repository.RamoAtividadeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RamoAtividadeService {

    private final RamoAtividadeRepository repository;
    private final RamoAtividadeMapper mapper;

    @Transactional
    public RamoAtividadeDTO salvar(RamoAtividadeDTO dto) {
        RamoAtividade entity = mapper.toEntity(dto);
        RamoAtividade saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<RamoAtividade> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<RamoAtividade> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public RamoAtividadeDTO atualizar(Long id, RamoAtividadeDTO dto) {
        RamoAtividade ramoExistente = repository.findById(id)
                .orElseThrow(()-> new EntidadeNaoEncontradaException("Ramo atividade não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, ramoExistente);
        repository.save(ramoExistente);
        return mapper.toDTO(ramoExistente);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
