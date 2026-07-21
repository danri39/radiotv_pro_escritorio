package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.PagamentosDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.PagamentosMapper;
import br.com.drs.radiotv_pro_escritorio.model.Agencia;
import br.com.drs.radiotv_pro_escritorio.model.Compras;
import br.com.drs.radiotv_pro_escritorio.model.ContratoPagamento;
import br.com.drs.radiotv_pro_escritorio.model.Pagamentos;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusPagamento;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPagamento;
import br.com.drs.radiotv_pro_escritorio.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagamentosService {

    private final PagamentosRepository repository;
    private final AgenciaRepository agenciaRepository;
    private final ContratoPagamentoRepository contratoPagamentoRepository;
    private final ComprasRepository comprasRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PagamentosMapper mapper;

    @Value("${diretorio.documentos.agencia:./documentos/agencia}")
    private String diretorioDocumentosAgencia;

    // ==========================================
    // 1. CRIAR PAGAMENTO MANUAL (Conta Diversa - Escritório)
    // ==========================================
    @Transactional
    public PagamentosDTO criarContaDiversa(PagamentosDTO dto) {
        validarDadosBasicos(dto);

        Pagamentos pagamento = mapper.toEntity(dto);
        pagamento.setTipoPagamento(TipoPagamento.CONTA_DIVERSSA);
        pagamento.setStatusPagamento(StatusPagamento.PENDENTE);
        pagamento.setAtivo(true);

        Pagamentos salvo = repository.save(pagamento);
        log.info("Conta diversa criada: {} - R$ {}", salvo.getDescricao(), salvo.getValor());
        return mapper.toDTO(salvo);
    }

    // ==========================================
    // 2. LANÇAR COMISSÃO DE AGÊNCIA (Automático - chamado pelo ContratoPagamentoService)
    // ==========================================
    /**
     * Este método é chamado AUTOMATICAMENTE quando o cliente paga uma parcela.
     * Cria um lançamento de comissão de agência com status AGUARDANDO_DOCUMENTO.
     * A agência precisará fazer upload da NF para que o pagamento seja liberado.
     */
    @Transactional
    public PagamentosDTO lancarComissaoAgencia(
            Agencia agencia,
            ContratoPagamento parcela,
            BigDecimal valorComissao,
            LocalDate dataLancamento) {

        // Evita duplicação
        if (repository.existsByTipoPagamentoAndContratoPagamento_ContratoPagamentoId(
                TipoPagamento.COMISSAO_AGENCIA, parcela.getContratoPagamentoId())) {
            log.warn("Comissão de agência já lançada para a parcela {}. Ignorando.", parcela.getContratoPagamentoId());
            return null;
        }

        String descricao = String.format(
                "Comissão Agência %s - Contrato %s - Parcela %d/%d",
                agencia.getNomeFantasia(),
                parcela.getContrato().getContratoId(),
                parcela.getNumeroParcela(),
                parcela.getContrato().getQuantidadeParcelas()
        );

        Pagamentos pagamento = Pagamentos.builder()
                .tipoPagamento(TipoPagamento.COMISSAO_AGENCIA)
                .statusPagamento(StatusPagamento.AGUARDANDO_DOCUMENTO)
                .descricao(descricao)
                .beneficiario(agencia.getNomeFantasia())
                .valor(valorComissao)
                .dataVencimento(dataLancamento)
                .agencia(agencia)
                .contratoPagamento(parcela)
                .ativo(true)
                .build();

        Pagamentos salvo = repository.save(pagamento);
        log.info("Comissão de agência lançada: {} - R$ {}", descricao, valorComissao);
        return mapper.toDTO(salvo);
    }

    // ==========================================
    // 3. LANÇAR PAGAMENTO DE COMPRA APROVADA (Automático - chamado pelo ComprasService)
    // ==========================================
    /**
     * Este método é chamado AUTOMATICAMENTE quando o administrador aprova uma compra.
     * Cria um lançamento de pagamento vinculado à compra.
     */
    @Transactional
    public PagamentosDTO lancarPagamentoCompra(Compras compra) {
        // Evita duplicação
        if (repository.existsByTipoPagamentoAndCompra_ComprasId(TipoPagamento.COMPRA_APROVADA, compra.getComprasId())) {
            log.warn("Pagamento já lançado para a compra {}. Ignorando.", compra.getComprasId());
            return null;
        }

        String descricao = String.format(
                "Compra aprovada - Solicitante: %s - Data: %s",
                compra.getFuncionario().getNome(),
                compra.getDataCompra()
        );

        Pagamentos pagamento = Pagamentos.builder()
                .tipoPagamento(TipoPagamento.COMPRA_APROVADA)
                .statusPagamento(StatusPagamento.PENDENTE)
                .descricao(descricao)
                .beneficiario(compra.getFuncionario().getNome())
                .valor(compra.getValorTotalGeral())
                .dataVencimento(LocalDate.now())
                .compra(compra)
                .funcionario(compra.getFuncionario())
                .ativo(true)
                .build();

        Pagamentos salvo = repository.save(pagamento);
        log.info("Pagamento de compra lançado: {} - R$ {}", descricao, compra.getValorTotalGeral());
        return mapper.toDTO(salvo);
    }

    // ==========================================
    // 4. LISTAGENS
    // ==========================================
    @Transactional(readOnly = true)
    public List<PagamentosDTO> listarTodos() {
        return mapper.toDTOList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public PagamentosDTO buscarPorId(Long id) {
        Pagamentos pagamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado com ID: " + id));
        return mapper.toDTO(pagamento);
    }

    @Transactional(readOnly = true)
    public List<PagamentosDTO> listarPorStatus(StatusPagamento status) {
        return mapper.toDTOList(repository.findByStatusPagamentoAndAtivoTrue(status));
    }

    @Transactional(readOnly = true)
    public List<PagamentosDTO> listarProntosParaPagamento() {
        return mapper.toDTOList(
                repository.findByStatusPagamentoAndAtivoTrueOrderByDataVencimentoAsc(StatusPagamento.PRONTO_PARA_PAGAMENTO)
        );
    }

    @Transactional(readOnly = true)
    public List<PagamentosDTO> listarPorTipo(TipoPagamento tipo) {
        return mapper.toDTOList(repository.findByTipoPagamentoAndAtivoTrue(tipo));
    }

    @Transactional(readOnly = true)
    public List<PagamentosDTO> listarPorAgencia(Long agenciaId) {
        return mapper.toDTOList(repository.buscarPorAgencia(agenciaId));
    }

    @Transactional(readOnly = true)
    public List<PagamentosDTO> listarPorFuncionario(Long funcionarioId) {
        return mapper.toDTOList(repository.buscarPorFuncionario(funcionarioId));
    }

    @Transactional(readOnly = true)
    public List<PagamentosDTO> listarPorPeriodoVencimento(LocalDate inicio, LocalDate fim) {
        return mapper.toDTOList(repository.buscarPorPeriodoVencimento(inicio, fim));
    }

    @Transactional(readOnly = true)
    public List<PagamentosDTO> listarPorPeriodoPagamento(LocalDate inicio, LocalDate fim) {
        return mapper.toDTOList(repository.buscarPorPeriodoPagamento(inicio, fim));
    }

    @Transactional(readOnly = true)
    public List<PagamentosDTO> listarVencidos() {
        LocalDate hoje = LocalDate.now();
        List<StatusPagamento> statusPendentes = List.of(
                StatusPagamento.PENDENTE,
                StatusPagamento.AGUARDANDO_DOCUMENTO,
                StatusPagamento.PRONTO_PARA_PAGAMENTO
        );
        return mapper.toDTOList(repository.buscarPagamentosVencidos(hoje, statusPendentes));
    }

    // ==========================================
    // 5. PORTAL DA AGÊNCIA: Listar comissões pendentes de documento
    // ==========================================
    @Transactional(readOnly = true)
    public List<PagamentosDTO> listarComissoesAgenciaAguardandoDocumento(Long agenciaId) {
        return mapper.toDTOList(
                repository.buscarComissoesAgenciaAguardandoDocumentoPorAgencia(agenciaId)
        );
    }

    // ==========================================
    // 6. PORTAL DA AGÊNCIA: Upload do Documento da NF
    // ==========================================
    /**
     * A agência faz upload da NF/Boleto. O sistema:
     * 1. Valida se o pagamento pertence à agência logada
     * 2. Salva o arquivo no disco
     * 3. Registra o número do documento e o caminho do arquivo
     * 4. Muda o status para PRONTO_PARA_PAGAMENTO
     */
    @Transactional
    public PagamentosDTO registrarDocumentoAgencia(
            Long pagamentoId,
            Long agenciaIdLogada,
            String numeroDocumento,
            MultipartFile arquivo) throws IOException {

        Pagamentos pagamento = repository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado com ID: " + pagamentoId));

        // TRAVA DE SEGURANÇA: A agência só pode fazer upload de pagamentos dela
        if (pagamento.getAgencia() == null || !pagamento.getAgencia().getAgenciaId().equals(agenciaIdLogada)) {
            throw new RuntimeException("Acesso negado: este pagamento não pertence à sua agência.");
        }

        if (pagamento.getTipoPagamento() != TipoPagamento.COMISSAO_AGENCIA) {
            throw new RuntimeException("Upload de documento permitido apenas para comissões de agência.");
        }

        // Salva o arquivo no disco
        String caminhoArquivo = salvarArquivoDocumento(arquivo, pagamentoId);

        // Registra o documento e muda o status
        pagamento.registrarDocumentoAgencia(numeroDocumento, caminhoArquivo);
        Pagamentos salvo = repository.save(pagamento);

        log.info("Documento da agência registrado para pagamento {}: NF {}", pagamentoId, numeroDocumento);
        return mapper.toDTO(salvo);
    }

    /**
     * Salva o arquivo no sistema de arquivos e retorna o caminho relativo.
     */
    private String salvarArquivoDocumento(MultipartFile arquivo, Long pagamentoId) throws IOException {
        Path diretorio = Paths.get(diretorioDocumentosAgencia);
        if (!Files.exists(diretorio)) {
            Files.createDirectories(diretorio);
        }

        String nomeOriginal = arquivo.getOriginalFilename();
        String extensao = nomeOriginal != null && nomeOriginal.contains(".")
                ? nomeOriginal.substring(nomeOriginal.lastIndexOf("."))
                : "";
        String nomeArquivo = "pagamento_" + pagamentoId + "_" + UUID.randomUUID().toString() + extensao;

        Path caminhoCompleto = diretorio.resolve(nomeArquivo);
        Files.copy(arquivo.getInputStream(), caminhoCompleto, StandardCopyOption.REPLACE_EXISTING);

        return caminhoCompleto.toString();
    }

    // ==========================================
    // 7. DAR BAIXA / PAGAR (Escritório)
    // ==========================================
    /**
     * O escritório dá baixa no pagamento.
     * Para comissões de agência, EXIGE que o documento já tenha sido registrado.
     */
    @Transactional
    public PagamentosDTO pagar(Long id, String formaPagamento) {
        Pagamentos pagamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado com ID: " + id));

        if (formaPagamento == null || formaPagamento.isBlank()) {
            throw new RuntimeException("A forma de pagamento é obrigatória.");
        }

        // O método helper da entidade valida as travas (comissão de agência precisa de documento)
        pagamento.marcarComoPago(LocalDate.now(), formaPagamento);
        Pagamentos salvo = repository.save(pagamento);

        log.info("Pagamento {} efetuado: {} - R$ {} via {}",
                id, pagamento.getDescricao(), pagamento.getValor(), formaPagamento);
        return mapper.toDTO(salvo);
    }

    // ==========================================
    // 8. CANCELAR PAGAMENTO (Administrador)
    // ==========================================
    @Transactional
    public void cancelar(Long id, String motivo) {
        Pagamentos pagamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado com ID: " + id));

        pagamento.cancelar();
        repository.save(pagamento);
        log.info("Pagamento {} cancelado. Motivo: {}", id, motivo);
    }

    // ==========================================
    // 9. EDITAR PAGAMENTO MANUAL (apenas contas diversas pendentes)
    // ==========================================
    @Transactional
    public PagamentosDTO editar(Long id, PagamentosDTO dto) {
        Pagamentos pagamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado com ID: " + id));

        if (pagamento.getStatusPagamento() == StatusPagamento.PAGO) {
            throw new RuntimeException("Não é possível editar um pagamento já efetuado.");
        }

        if (pagamento.getTipoPagamento() != TipoPagamento.CONTA_DIVERSSA) {
            throw new RuntimeException("Apenas contas diversas podem ser editadas manualmente.");
        }

        pagamento.setDescricao(dto.getDescricao());
        pagamento.setBeneficiario(dto.getBeneficiario());
        pagamento.setValor(dto.getValor());
        pagamento.setDataVencimento(dto.getDataVencimento());
        pagamento.setObservacao(dto.getObservacao());

        Pagamentos salvo = repository.save(pagamento);
        return mapper.toDTO(salvo);
    }

    // ==========================================
    // 10. JOB: Marcar pagamentos vencidos
    // ==========================================
    /**
     * Job diário que poderia ser usado para gerar relatórios de atraso.
     * Como o status não tem "VENCIDO" (usamos a data de vencimento para cálculo),
     * este método apenas retorna a lista de pagamentos vencidos para o painel admin.
     */
    @Transactional(readOnly = true)
    public List<PagamentosDTO> listarPagamentosEmAtraso() {
        return listarVencidos();
    }

    // ==========================================
    // MÉTODO HELPER: Validação de dados básicos
    // ==========================================
    private void validarDadosBasicos(PagamentosDTO dto) {
        if (dto.getDescricao() == null || dto.getDescricao().isBlank()) {
            throw new RuntimeException("A descrição é obrigatória.");
        }
        if (dto.getBeneficiario() == null || dto.getBeneficiario().isBlank()) {
            throw new RuntimeException("O beneficiário é obrigatório.");
        }
        if (dto.getValor() == null || dto.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O valor deve ser maior que zero.");
        }
        if (dto.getDataVencimento() == null) {
            throw new RuntimeException("A data de vencimento é obrigatória.");
        }
    }
}