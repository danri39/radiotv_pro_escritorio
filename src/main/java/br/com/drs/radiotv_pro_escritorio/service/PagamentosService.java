package br.com.drs.radiotv_pro_escritorio.service;

import br.com.drs.radiotv_pro_escritorio.dto.PagamentosDTO;
import br.com.drs.radiotv_pro_escritorio.dto.PagamentosRequestDTO;
import br.com.drs.radiotv_pro_escritorio.mapper.PagamentosMapper;
import br.com.drs.radiotv_pro_escritorio.model.Compras;
import br.com.drs.radiotv_pro_escritorio.model.Pagamentos;
import br.com.drs.radiotv_pro_escritorio.model.enuns.StatusPagamento;
import br.com.drs.radiotv_pro_escritorio.model.enuns.TipoPagamento;
import br.com.drs.radiotv_pro_escritorio.repository.ComprasRepository;
import br.com.drs.radiotv_pro_escritorio.repository.PagamentosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagamentosService {

    private final PagamentosRepository pagamentosRepository;
    private final ComprasRepository comprasRepository;
    private final PagamentosMapper mapper;

    @Transactional
    public PagamentosDTO criarPagamentoManual(PagamentosRequestDTO dto) {
        if (dto.tipo() != TipoPagamento.MANUAL) {
            throw new IllegalArgumentException("Use este método apenas para pagamentos manuais.");
        }

        Pagamentos novoPagamento = mapper.toEntity(dto);
        novoPagamento.setStatus(StatusPagamento.PENDENTE);

        Pagamentos salvo = pagamentosRepository.save(novoPagamento);
        return mapper.toDTO(salvo);
    }

    @Transactional
    public PagamentosDTO gerarPagamentoParaCompraAprovada(Long comprasId, BigDecimal valor) {
        Compras compra = comprasRepository.findById(comprasId)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada"));

        Pagamentos pagamentoCompra = Pagamentos.builder()
                .compras(compra)
                .tipo(TipoPagamento.COMPRA)
                .valor(valor)
                .descricao("Pagamento referente à compra #" + comprasId)
                .status(StatusPagamento.PENDENTE)
                .build();

        Pagamentos salvo = pagamentosRepository.save(pagamentoCompra);
        return mapper.toDTO(salvo);
    }

    @Transactional
    public PagamentosDTO atualizarPagamento(Long id, PagamentosRequestDTO dto) {
        Pagamentos pagamentos = pagamentosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        mapper.updateEntityFromDto(dto, pagamentos);

        if (dto.comprasId() != null) {
            Compras compra = comprasRepository.findById(dto.comprasId())
                    .orElseThrow(() -> new RuntimeException("Compra não encontrada"));
            pagamentos.setCompras(compra);
        }

        Pagamentos salvo = pagamentosRepository.save(pagamentos);
        return mapper.toDTO(salvo);
    }

    @Transactional
    public PagamentosDTO darBaixa(Long pagamentosId) {
        Pagamentos pagamentos = pagamentosRepository.findById(pagamentosId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        if (pagamentos.getStatus() == StatusPagamento.PAGO) {
            throw new IllegalStateException("Este pagamento já foi baixado/pago.");
        }

        pagamentos.setStatus(StatusPagamento.PAGO);
        pagamentos.setDataPagamento(LocalDateTime.now());

        Pagamentos salvo = pagamentosRepository.save(pagamentos);
        return mapper.toDTO(salvo);
    }

    public List<PagamentosDTO> listarTodos() {
        return pagamentosRepository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public List<PagamentosDTO> listarPendentes() {
        return pagamentosRepository.findByStatus(StatusPagamento.PENDENTE).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional
    public PagamentosDTO gerarPagamentoComissaoAgencia(Long agenciaId, String nomeFantasia,
                                                       BigDecimal valor, String descricaoBase,
                                                       String banco, String agenciaBancaria, String conta) {
        String descricaoCompleta = String.format("%s | Agência: %s | Banco: %s | Ag: %s | CC: %s",
                descricaoBase, nomeFantasia, banco, agenciaBancaria, conta);

        PagamentosRequestDTO dto = new PagamentosRequestDTO(
                null, // comprasId nulo
                TipoPagamento.MANUAL,
                valor,
                descricaoCompleta
        );
        return criarPagamentoManual(dto);
    }
}