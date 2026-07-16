package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoMidiaDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.ContratoMidiaMapper;
import br.com.drs.radiotv_pro_escritorio.model.ContratoMidia;
import br.com.drs.radiotv_pro_escritorio.repository.ContratoMidiaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContratoMidiaService {

    private final ContratoMidiaRepository repository;
    private final ContratoMidiaMapper mapper;

    @Transactional
    public ContratoMidiaDTO salvar(ContratoMidiaDTO dto) {
        ContratoMidia entity = mapper.toEntity(dto);
        ContratoMidia saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<ContratoMidia> listarTodos()  {
        return repository.findAll();
    }

    @Transactional
    public Optional<ContratoMidia> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ContratoMidiaDTO atualizar(Long id, ContratoMidiaDTO dto) {
        ContratoMidia existente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Contrato mídia não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, existente);
        repository.save(existente);
        return mapper.toDTO(existente);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}