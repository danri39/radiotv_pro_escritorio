package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.ComissaoVendedorDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.ComissaoVendedorMapper;
import br.com.drs.radiotv_pro_escritorio.model.ComissaoVendedor;
import br.com.drs.radiotv_pro_escritorio.model.ContratoPagamento;
import br.com.drs.radiotv_pro_escritorio.model.Vendedor;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusComissao;
import br.com.drs.radiotv_pro_escritorio.repository.ComissaoVendedorRepository;
import br.com.drs.radiotv_pro_escritorio.repository.ContratoPagamentoRepository;
import br.com.drs.radiotv_pro_escritorio.repository.VendedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComissaoVendedorService {

    private final ComissaoVendedorRepository repository;
    private final VendedorRepository vendedorRepository;
    private final ContratoPagamentoRepository contratoPagamentoRepository;
    private final ComissaoVendedorMapper mapper;

    // Formato padrão para o mês de referência
    private static final DateTimeFormatter FORMATO_MES = DateTimeFormatter.ofPattern("MM/yyyy");

    // ==========================================
    // 1. LANÇAR COMISSÃO (Automático - chamado pelo ContratoPagamentoService)
    // ==========================================
    /**
     * Este método é chamado AUTOMATICAMENTE quando o cliente paga uma parcela.
     * Calcula a comissão com base no percentual do vendedor e no valor efetivamente recebido.
     *
     * @param vendedor O vendedor que fez a venda
     * @param parcela A parcela que foi paga pelo cliente
     * @param valorRecebido O valor que o cliente efetivamente pagou (pode ter juros/descontos)
     * @return A comissão criada, ou null se já existia (duplicação)
     */
    @Transactional
    public ComissaoVendedorDTO lancarComissao(Vendedor vendedor, ContratoPagamento parcela, BigDecimal valorRecebido) {

        // TRAVA: Evita duplicação
        if (repository.existsByContratoPagamento_ContratoPagamentoIdAndAtivaTrue(parcela.getContratoPagamentoId())) {
            log.warn("Comissão já lançada para a parcela {}. Ignorando duplicação.", parcela.getContratoPagamentoId());
            return null;
        }

        // Busca o percentual de comissão do vendedor
        int percentual = vendedor.getComissaoVendas();
        if (percentual <= 0) {
            log.warn("Vendedor {} não tem percentual de comissão válido. Ignorando.", vendedor.getVendedorId());
            return null;
        }

        // Calcula o valor da comissão
        BigDecimal valorComissao = valorRecebido
                .multiply(BigDecimal.valueOf(percentual))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Define o mês de referência (mês em que o cliente pagou)
        String mesReferencia = LocalDate.now().format(FORMATO_MES);

        ComissaoVendedor comissao = ComissaoVendedor.builder()
                .vendedor(vendedor)
                .contratoPagamento(parcela)
                .mesReferencia(mesReferencia)
                .valorComissao(valorComissao)
                .percentualAplicado(percentual)
                .dataCalculo(LocalDate.now())
                .statusComissao(StatusComissao.PENDENTE)
                .ativa(true)
                .build();

        ComissaoVendedor salva = repository.save(comissao);

        log.info("Comissão lançada: Vendedor {} - Parcela {} - Valor R$ {} ({}%)",
                vendedor.getFuncionario().getNome(),
                parcela.getNumeroParcela(),
                valorComissao,
                percentual);

        return mapper.toDTO(salva);
    }

    // ==========================================
    // 2. LISTAGENS GERAIS
    // ==========================================
    @Transactional(readOnly = true)
    public List<ComissaoVendedorDTO> listarTodas() {
        return mapper.toDTOList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public ComissaoVendedorDTO buscarPorId(Long id) {
        ComissaoVendedor comissao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comissão não encontrada com ID: " + id));
        return mapper.toDTO(comissao);
    }

    @Transactional(readOnly = true)
    public List<ComissaoVendedorDTO> listarPorVendedor(Long vendedorId) {
        return mapper.toDTOList(repository.buscarPorVendedor(vendedorId));
    }

    @Transactional(readOnly = true)
    public List<ComissaoVendedorDTO> listarPorVendedorEStatus(Long vendedorId, StatusComissao status) {
        return mapper.toDTOList(repository.buscarPorVendedorEStatus(vendedorId, status));
    }

    @Transactional(readOnly = true)
    public List<ComissaoVendedorDTO> listarPorStatus(StatusComissao status) {
        return mapper.toDTOList(repository.findByStatusComissaoAndAtivaTrue(status));
    }

    @Transactional(readOnly = true)
    public List<ComissaoVendedorDTO> listarPorContrato(Long contratoId) {
        return mapper.toDTOList(repository.buscarPorContrato(contratoId));
    }

    @Transactional(readOnly = true)
    public List<ComissaoVendedorDTO> listarPorPeriodoCalculo(LocalDate inicio, LocalDate fim) {
        return mapper.toDTOList(repository.buscarPorPeriodoCalculo(inicio, fim));
    }

    @Transactional(readOnly = true)
    public List<ComissaoVendedorDTO> listarPorPeriodoProcessamento(LocalDate inicio, LocalDate fim) {
        return mapper.toDTOList(repository.buscarPorPeriodoProcessamento(inicio, fim));
    }

    // ==========================================
    // 3. LISTAGENS PARA FOLHA DE PAGAMENTO
    // ==========================================
    @Transactional(readOnly = true)
    public List<ComissaoVendedorDTO> listarPendentesPorVendedorEMes(Long vendedorId, String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return mapper.toDTOList(repository.buscarPendentesPorVendedorEMes(vendedorId, mesReferencia));
    }

    @Transactional(readOnly = true)
    public List<ComissaoVendedorDTO> listarPendentesPorMes(String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return mapper.toDTOList(repository.buscarPendentesPorMes(mesReferencia));
    }

    @Transactional(readOnly = true)
    public BigDecimal somarComissoesPendentes(Long vendedorId, String mesReferencia) {
        validarFormatoMes(mesReferencia);
        return repository.somarComissoesPendentesPorVendedorEMes(vendedorId, mesReferencia);
    }

    // ==========================================
    // 4. PROCESSAR EM LOTE (Chamado pelo FolhaPagamentoService)
    // ==========================================
    /**
     * Processa todas as comissões pendentes de um vendedor em um mês de referência.
     * Marca cada uma como PROCESSADA e vincula à folha de pagamento.
     *
     * @param vendedorId ID do vendedor
     * @param mesReferencia Mês de referência (ex: "07/2026")
     * @param folhaPagamentoId ID da folha de pagamento que está sendo fechada
     * @return Lista de comissões processadas
     */
    @Transactional
    public List<ComissaoVendedorDTO> processarEmLote(Long vendedorId, String mesReferencia, Long folhaPagamentoId) {
        validarFormatoMes(mesReferencia);

        List<ComissaoVendedor> comissoes = repository.buscarPendentesPorVendedorEMes(vendedorId, mesReferencia);

        if (comissoes.isEmpty()) {
            log.info("Nenhuma comissão pendente encontrada para o vendedor {} no mês {}", vendedorId, mesReferencia);
            return List.of();
        }

        for (ComissaoVendedor comissao : comissoes) {
            comissao.processarNaFolha(folhaPagamentoId);
        }

        repository.saveAll(comissoes);

        log.info("{} comissões do vendedor {} processadas na folha {} (mês {})",
                comissoes.size(), vendedorId, folhaPagamentoId, mesReferencia);

        return mapper.toDTOList(comissoes);
    }

    // ==========================================
    // 5. CANCELAR COMISSÃO (Em caso de estorno do cliente)
    // ==========================================
    @Transactional
    public void cancelar(Long id, String motivo) {
        ComissaoVendedor comissao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comissão não encontrada com ID: " + id));

        comissao.cancelar();
        repository.save(comissao);

        log.info("Comissão {} cancelada. Motivo: {}", id, motivo);
    }

    // ==========================================
    // 6. DASHBOARD / CONTAGENS
    // ==========================================
    @Transactional(readOnly = true)
    public long contarPendentesPorVendedor(Long vendedorId) {
        return repository.countByVendedor_VendedorIdAndStatusComissaoAndAtivaTrue(vendedorId, StatusComissao.PENDENTE);
    }

    @Transactional(readOnly = true)
    public long contarPendentesTotal() {
        return repository.countByStatusComissaoAndAtivaTrue(StatusComissao.PENDENTE);
    }

    @Transactional(readOnly = true)
    public long contarProcessadasTotal() {
        return repository.countByStatusComissaoAndAtivaTrue(StatusComissao.PROCESSADA);
    }

    // ==========================================
    // MÉTODO HELPER: Validação do formato do mês
    // ==========================================
    private void validarFormatoMes(String mesReferencia) {
        if (mesReferencia == null || !mesReferencia.matches("\\d{2}/\\d{4}")) {
            throw new RuntimeException("Formato de mês inválido. Use 'MM/yyyy' (ex: '07/2026').");
        }
    }
}