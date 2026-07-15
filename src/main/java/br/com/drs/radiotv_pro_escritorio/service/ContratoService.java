package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.mapper.ContratoMapper;
import br.com.drs.radiotv_pro_escritorio.model.Contrato;
import br.com.drs.radiotv_pro_escritorio.repository.ContratoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoRepository repository;
    private final ContratoMapper mapper;

    @Transactional
    public ContratoDTO salvar(ContratoDTO dto) {
        desativarContratosVencidos();
        Contrato entity = mapper.toEntity(dto);
        Contrato saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<Contrato> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<Contrato> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ContratoDTO atualizar(Long id, ContratoDTO dto) {
        Contrato contratoExistente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, contratoExistente);
        repository.save(contratoExistente);
        return mapper.toDTO(contratoExistente);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }

    private void desativarContratosVencidos() {
        LocalDate hoje = LocalDate.now();

        List<Contrato> contratos = repository.findAll();

        for (Contrato contrato : contratos) {
            if (contrato.getDataFinal() != null
                    && contrato.getDataFinal().isBefore(hoje)
                    && Boolean.TRUE.equals(contrato.getAtivo())) {

                contrato.setAtivo(false);
            }
        }

        repository.saveAll(contratos);
    }
}
