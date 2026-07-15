package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ClienteDTO;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import br.com.drs.radiotv_pro_escritorio.mapper.ClienteMapper;
import br.com.drs.radiotv_pro_escritorio.model.Cliente;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPessoa;
import br.com.drs.radiotv_pro_escritorio.repository.ClienteRepository;
import br.com.drs.radiotv_pro_escritorio.util.DocumentoUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;
    private final ClienteMapper mapper;

    @Transactional
    public ClienteDTO salvar(ClienteDTO dto) {
        TipoPessoa tipo = dto.getTipoPessoa();
        if(TipoPessoa.JURIDICA.equals(tipo)) {
            if(!DocumentoUtils.isCNPJ(dto.getCnpj())) {
                throw new RegraNegocioException("CNPJ Inválido!");
            }
        } else if (TipoPessoa.FISICA.equals(tipo)) {
            if(!DocumentoUtils.isCPF(dto.getCpf())) {
                throw  new RegraNegocioException("CPF Inválido!");
            }
        } else {
            throw new RegraNegocioException("Tipo de pessoa inválido: " + (tipo != null ? tipo.name() : "bull"));
        }
        Cliente entity = mapper.toEntity(dto);
        Cliente saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Optional<Cliente> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ClienteDTO atualizar(Long id, ClienteDTO dto) {
        Cliente clienteExistente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado com o ID: " + id));
        mapper.updateEntityFromDto(dto, clienteExistente);
        repository.save(clienteExistente);
        return mapper.toDTO(clienteExistente);
    }

    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}
