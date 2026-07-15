package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ProgramaDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.ProgramaMapper;
import br.com.drs.radiotv_pro_escritorio.model.Programa;
import br.com.drs.radiotv_pro_escritorio.repository.ProgramaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class ProgramaService {

    private final ProgramaRepository repository;
    private final ProgramaMapper mapper;

    @Transactional
    public ProgramaDTO salvar(ProgramaDTO dto) {
        Programa entity = mapper.toEntity(dto);
        Programa saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<Programa> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<Programa> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ProgramaDTO atualizar(Long is, ProgramaDTO dto) {
        Programa programaExistente = repository.findById(dto.getProgramaId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Programa não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, programaExistente);
        repository.save(programaExistente);
        return mapper.toDTO(programaExistente);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
