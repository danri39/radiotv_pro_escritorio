package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.ContratoMapper;
import br.com.drs.radiotv_pro_escritorio.model.Contrato;
import br.com.drs.radiotv_pro_escritorio.model.ContratoPagamento;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusRecebimento;
import br.com.drs.radiotv_pro_escritorio.repository.ContratoPagamentoRepository;
import br.com.drs.radiotv_pro_escritorio.repository.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.drs.radiotv_pro_escritorio.exception.EntidadeNaoEncontradaException;
import br.com.drs.radiotv_pro_escritorio.exception.RegraNegocioException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoRepository repository;
    private final ContratoMapper mapper;
    private final ContratoPagamentoRepository contratoPagamentoRepository;

    @Transactional
    public ContratoDTO salvar(ContratoDTO dto) {
        desativarContratosVencidos();
        Contrato entity = mapper.toEntity(dto);
        Contrato saved = repository.save(entity);
        gerarParcelas(dto, saved);
        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<Contrato> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Contrato> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ContratoDTO atualizar(Long id, ContratoDTO dto) {
        Contrato contratoExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado com o ID: " + id)); // Corrigido de "Usuário" para "Contrato"

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

    /**
     * Gera as parcelas do contrato usando a nova estrutura da entidade ContratoPagamento.
     */
    private void gerarParcelas(ContratoDTO dto, Contrato contrato) {
        List<ContratoPagamento> parcelas = new ArrayList<>();
        LocalDate dataVencimento = dto.getDataPrimeiroPagamento();

        for (int i = 1; i <= dto.getQuantidadeParcelas(); i++) {
            ContratoPagamento parcela = ContratoPagamento.builder()
                    .contrato(contrato)
                    .numeroParcela(i) // Define o número da parcela (1, 2, 3...)
                    .dataVencimento(dataVencimento) // Usa dataVencimento em vez de dataPagamentoReal
                    .valorParcela(dto.getValorParcelas())
                    .statusRecebimento(StatusRecebimento.A_FATURAR) // Usa o Enum em vez de faturado/paga
                    .comissaoVendedorLancada(false)
                    .comissaoAgenciaLancada(false)
                    .ativo(true)
                    .build();

            parcelas.add(parcela);
            dataVencimento = dataVencimento.plusMonths(1); // Avança um mês para a próxima parcela
        }

        contratoPagamentoRepository.saveAll(parcelas);
    }
}
