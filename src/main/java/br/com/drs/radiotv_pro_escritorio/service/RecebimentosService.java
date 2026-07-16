package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.GerarParcelasRequestDTO;
import br.com.drs.radiotv_pro_escritorio.dto.RecebimentosResponseDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.RecebimentosMapper;
import br.com.drs.radiotv_pro_escritorio.model.*;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusRecebimento;
import br.com.drs.radiotv_pro_escritorio.repository.ComissoesVendedorRepository;
import br.com.drs.radiotv_pro_escritorio.repository.ContratoRepository;
import br.com.drs.radiotv_pro_escritorio.repository.RecebimentosRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecebimentosService {

    private final RecebimentosRepository repository;
    private final ContratoRepository contratoRepository;
    private final PagamentosService pagamentosService;
    private final ComissoesVendedorRepository comissoesRepository;
    private final RecebimentosMapper mapper;

    @Transactional
    public List<RecebimentosResponseDTO> gerarParcelas(GerarParcelasRequestDTO dto) {
        Contrato contrato = contratoRepository.findById(dto.contratoId())
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        BigDecimal valorParcela = dto.valorTotal()
                .divide(BigDecimal.valueOf(dto.quantidadeParcelas()), 2, RoundingMode.HALF_UP);

        List<Recebimentos> parcelas = new ArrayList<>();
        LocalDate dataVenc = dto.dataPrimeiroVencimento();

        for (int i = 1; i <= dto.quantidadeParcelas(); i++) {
            Recebimentos parcela = Recebimentos.builder()
                    .contrato(contrato)
                    .numeroParcela(i)
                    .valor(valorParcela)
                    .dataVencimento(dataVenc)
                    .status(StatusRecebimento.PENDENTE)
                    .build();
            parcelas.add(parcela);
            dataVenc = dataVenc.plusMonths(1); // próxima parcela no mês seguinte
        }

        return repository.saveAll(parcelas).stream()
                .map(mapper::toDTO)
                .toList();
    }

    // 2. Dar baixa no recebimento (cliente pagou)
    @Transactional
    public RecebimentosResponseDTO darBaixa(Long recebimentosId) {
        Recebimentos parcela = repository.findById(recebimentosId)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        if (parcela.getStatus() == StatusRecebimento.RECEBIDO) {
            throw new IllegalStateException("Parcela já foi recebida.");
        }

        Contrato contrato = parcela.getContrato();
        BigDecimal valorComissaoVendedor = BigDecimal.ZERO;
        BigDecimal valorComissaoAgencia = BigDecimal.ZERO;

        // === COMISSÃO DO VENDEDOR ===
        // Só paga se o contrato NÃO for sem comissão (ex: prefeitura)
        if (contrato.getVendedor() != null && !Boolean.TRUE.equals(contrato.getSemComissao())) {
            Vendedor vendedor = contrato.getVendedor();
            // comissaoVendas é int (ex: 10 = 10%)
            valorComissaoVendedor = parcela.getValor()
                    .multiply(BigDecimal.valueOf(vendedor.getComissaoVendas()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Acumula para o próximo pagamento (folha/vale)
            acumularComissaoVendedor(vendedor, parcela, valorComissaoVendedor);
        }

        // === COMISSÃO DA AGÊNCIA ===
        if (contrato.getAgencia() != null) {
            Agencia agencia = contrato.getAgencia();
            valorComissaoAgencia = parcela.getValor()
                    .multiply(BigDecimal.valueOf(agencia.getComissaoVendas()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Cria um Pagamento para a agência (com os dados bancários dela)
            pagamentosService.gerarPagamentoComissaoAgencia(
                    agencia.getAgenciaId(),
                    agencia.getNomeFantasia(),
                    valorComissaoAgencia,
                    "Comissão parcela " + parcela.getNumeroParcela() +
                            " - Contrato #" + contrato.getContratoId(),
                    agencia.getBanco(),
                    agencia.getAgencia(),
                    agencia.getConta()
            );
        }

        // Atualiza a parcela
        parcela.setStatus(StatusRecebimento.RECEBIDO);
        parcela.setDataRecebimento(LocalDate.now());
        parcela.setComissaoVendedor(valorComissaoVendedor);
        parcela.setComissaoAgencia(valorComissaoAgencia);

        return mapper.toDTO(repository.save(parcela));
    }

    private void acumularComissaoVendedor(Vendedor vendedor, Recebimentos parcela, BigDecimal valor) {
        ComissoesVendedor comissao = ComissoesVendedor.builder()
                .vendedor(vendedor)
                .recebimentos(parcela)
                .valor(valor)
                .dataCalculo(LocalDate.now())
                .paga(false)
                .build();
        comissoesRepository.save(comissao);
    }

    public List<RecebimentosResponseDTO> listarPorContrato(Long contratoId) {
        return repository.findByContratoContratoId(contratoId).stream()
                .map(mapper::toDTO).toList();
    }

    @Transactional
    public BigDecimal fecharComissoesVendedor(Long vendedorId) {
        // Busca todas as comissões acumuladas e ainda não pagas
        List<ComissoesVendedor> comissoesPendentes =
                comissoesRepository.findByVendedorVendedorIdAndPagaFalse(vendedorId);

        if (comissoesPendentes.isEmpty()) {
            throw new RuntimeException("Nenhuma comissão pendente para este vendedor.");
        }

        // Soma tudo
        BigDecimal total = comissoesPendentes.stream()
                .map(ComissoesVendedor::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Aqui você pode:
        // 1. Criar um registro de "vale" ou lançar na folha
        // 2. Marcar todas como pagas
        comissoesPendentes.forEach(c -> c.setPaga(true));
        comissoesRepository.saveAll(comissoesPendentes);

        return total; // Retorna o valor que será pago ao vendedor
    }
}