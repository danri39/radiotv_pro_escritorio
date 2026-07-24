package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ContratoDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.ContratoMapper;
import br.com.drs.radiotv_pro_escritorio.model.*;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusRecebimento;
import br.com.drs.radiotv_pro_escritorio.repository.*;
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
    private final ClienteRepository clienteRepository;
    private final VendedorRepository vendedorRepository;
    private final AgenciaRepository agenciaRepository;

    @Transactional
    public ContratoDTO salvar(ContratoDTO dto) {
        desativarContratosVencidos();

        Contrato contrato = new Contrato();

        // Buscar e setar o cliente
        if (dto.getCliente() != null && dto.getCliente().getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getCliente().getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            contrato.setCliente(cliente);
        }

        // Buscar e setar o vendedor
        if (dto.getVendedor() != null && dto.getVendedor().getVendedorId() != null) {
            Vendedor vendedor = vendedorRepository.findById(dto.getVendedor().getVendedorId())
                    .orElseThrow(() -> new RuntimeException("Vendedor não encontrado"));
            contrato.setVendedor(vendedor);
        }

        // Buscar e setar a agência (opcional)
        if (dto.getAgencia() != null && dto.getAgencia().getAgenciaId() != null) {
            Agencia agencia = agenciaRepository.findById(dto.getAgencia().getAgenciaId())
                    .orElse(null);
            contrato.setAgencia(agencia);
        }

        // Setar os outros campos
        contrato.setDataInicio(dto.getDataInicio());
        contrato.setDataFinal(dto.getDataFinal());
        contrato.setValorTotal(dto.getValorTotal());
        contrato.setQuantidadeParcelas(dto.getQuantidadeParcelas());
        contrato.setValorParcelas(dto.getValorParcelas());
        contrato.setDataPrimeiroPagamento(dto.getDataPrimeiroPagamento());
        contrato.setContratoBonificado(dto.getContratoBonificado() != null ? dto.getContratoBonificado() : false);
        contrato.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);

        Contrato saved = repository.save(contrato);

        // Gerar parcelas se tiver dados
        if (dto.getQuantidadeParcelas() != null && dto.getQuantidadeParcelas() > 0
                && dto.getValorParcelas() != null && dto.getDataPrimeiroPagamento() != null) {
            gerarParcelas(dto, saved);
        }

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
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado com o ID: " + id));

        // Atualizar apenas os campos permitidos manualmente
        if (dto.getDataInicio() != null) contratoExistente.setDataInicio(dto.getDataInicio());
        if (dto.getDataFinal() != null) contratoExistente.setDataFinal(dto.getDataFinal());
        if (dto.getValorTotal() != null) contratoExistente.setValorTotal(dto.getValorTotal());
        if (dto.getQuantidadeParcelas() != null) contratoExistente.setQuantidadeParcelas(dto.getQuantidadeParcelas());
        if (dto.getValorParcelas() != null) contratoExistente.setValorParcelas(dto.getValorParcelas());
        if (dto.getDataPrimeiroPagamento() != null) contratoExistente.setDataPrimeiroPagamento(dto.getDataPrimeiroPagamento());
        if (dto.getContratoBonificado() != null) contratoExistente.setContratoBonificado(dto.getContratoBonificado());
        if (dto.getAtivo() != null) contratoExistente.setAtivo(dto.getAtivo());

        // NÃO atualizar: contratoId, cliente, vendedor, agencia

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
