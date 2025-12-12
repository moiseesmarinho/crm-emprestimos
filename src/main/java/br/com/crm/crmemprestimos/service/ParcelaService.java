package br.com.crm.crmemprestimos.service;

import br.com.crm.crmemprestimos.dto.ParcelaPagamentoRequest;
import br.com.crm.crmemprestimos.exception.RegraNegocioException;
import br.com.crm.crmemprestimos.model.Emprestimo;
import br.com.crm.crmemprestimos.model.Parcela;
import br.com.crm.crmemprestimos.model.ParcelaStatus;
import br.com.crm.crmemprestimos.model.StatusEmprestimo;
import br.com.crm.crmemprestimos.repository.EmprestimoRepository;
import br.com.crm.crmemprestimos.repository.ParcelaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class ParcelaService {

    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    private final ParcelaRepository parcelaRepository;
    private final EmprestimoRepository emprestimoRepository;

    public ParcelaService(ParcelaRepository parcelaRepository,
                          EmprestimoRepository emprestimoRepository) {
        this.parcelaRepository = parcelaRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    // ============================================================
    // CREATE
    // ============================================================
    public Parcela criar(Long emprestimoId, Parcela parcela) {
        if (emprestimoId == null) {
            throw new IllegalArgumentException("emprestimoId não pode ser nulo");
        }
        if (parcela == null) {
            throw new IllegalArgumentException("parcela não pode ser nula");
        }

        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RegraNegocioException("Empréstimo não encontrado"));

        parcela.setEmprestimo(emprestimo);

        if (parcela.getStatus() == null) {
            parcela.setStatus(ParcelaStatus.PENDENTE);
        }

        return parcelaRepository.save(parcela);
    }

    // ============================================================
    // READ
    // ============================================================
    public List<Parcela> listarTodas() {
        return parcelaRepository.findAll();
    }

    public Parcela buscarPorId(Long id) {
        return parcelaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Parcela não encontrada"));
    }

    public List<Parcela> listarPorEmprestimo(Long emprestimoId) {
        if (emprestimoId == null) {
            throw new IllegalArgumentException("emprestimoId não pode ser nulo");
        }
        return parcelaRepository.findByEmprestimoIdOrderByNumeroParcelaAsc(emprestimoId);
    }

    // ============================================================
    // PAGAR PARCELA PELO NÚMERO
    // ROTA: /emprestimos/{id}/parcelas/{numero}/pagar
    // ============================================================
    @Transactional
    public Parcela pagarParcela(Long emprestimoId, Integer numeroParcela) {
        if (emprestimoId == null || numeroParcela == null) {
            throw new IllegalArgumentException("emprestimoId e numeroParcela são obrigatórios");
        }

        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RegraNegocioException("Empréstimo não encontrado"));

        if (emprestimo.getStatus() == StatusEmprestimo.QUITADO ||
            emprestimo.getStatus() == StatusEmprestimo.CANCELADO) {
            throw new RegraNegocioException("Empréstimo encerrado");
        }

        Parcela parcela = parcelaRepository
                .findByEmprestimoIdAndNumeroParcela(emprestimoId, numeroParcela)
                .orElseThrow(() ->
                        new RegraNegocioException("Parcela não encontrada para o empréstimo " +
                                emprestimoId + " e número " + numeroParcela)
                );

        if (parcela.getStatus() == ParcelaStatus.PAGA) {
            throw new RegraNegocioException("Esta parcela já foi paga");
        }

        BigDecimal valor = parcela.getValorPrevisto() != null
                ? parcela.getValorPrevisto()
                : BigDecimal.ZERO;

        return aplicarPagamento(parcela, valor, LocalDate.now(), "Pagamento realizado");
    }

    // ============================================================
    // PAGAR PARCELA PELO ID
    // ROTA: /parcelas/{id}/pagamento
    // ============================================================
    @Transactional
    public Parcela registrarPagamento(Long parcelaId, ParcelaPagamentoRequest request) {
        if (parcelaId == null) {
            throw new IllegalArgumentException("parcelaId não pode ser nulo");
        }
        if (request == null) {
            throw new IllegalArgumentException("request não pode ser nula");
        }

        Parcela parcela = parcelaRepository.findById(parcelaId)
                .orElseThrow(() -> new RegraNegocioException("Parcela não encontrada"));

        Emprestimo emprestimo = parcela.getEmprestimo();

        if (emprestimo.getStatus() == StatusEmprestimo.QUITADO ||
            emprestimo.getStatus() == StatusEmprestimo.CANCELADO) {
            throw new RegraNegocioException("Empréstimo encerrado");
        }

        if (parcela.getStatus() == ParcelaStatus.PAGA) {
            throw new RegraNegocioException("Esta parcela já foi paga");
        }

        BigDecimal valorPago = request.getValorPago() != null
                ? request.getValorPago()
                : parcela.getValorPrevisto();

        if (valorPago == null || valorPago.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Valor do pagamento deve ser maior que zero");
        }

        LocalDate dataPagamento = request.getDataPagamento() != null
                ? request.getDataPagamento()
                : LocalDate.now();

        return aplicarPagamento(parcela, valorPago, dataPagamento, request.getObservacoes());
    }

    // ============================================================
    // REGRA CENTRAL DE PAGAMENTO
    // ============================================================
    @Transactional
    protected Parcela aplicarPagamento(Parcela parcela,
                                      BigDecimal valorPago,
                                      LocalDate dataPagamento,
                                      String observacoes) {

        Emprestimo emprestimo = parcela.getEmprestimo();

        // Atualiza parcela
        parcela.setValorPago(valorPago);
        parcela.setDataPagamento(dataPagamento);
        parcela.setObservacoes(observacoes);

        parcela.setStatus(ParcelaStatus.PAGA);

        // Atualiza saldo devedor
        BigDecimal saldoAtual = emprestimo.getSaldoDevedor() != null
                ? emprestimo.getSaldoDevedor()
                : BigDecimal.ZERO;

        BigDecimal novoSaldo = saldoAtual.subtract(valorPago, MC);

        if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            novoSaldo = BigDecimal.ZERO;
        }

        emprestimo.setSaldoDevedor(novoSaldo);

        if (novoSaldo.compareTo(BigDecimal.ZERO) == 0) {
            emprestimo.setStatus(StatusEmprestimo.QUITADO);
            emprestimo.setDataEncerramento(LocalDate.now());
        }

        parcelaRepository.save(parcela);
        emprestimoRepository.save(emprestimo);

        return parcela;
    }

    // ============================================================
    // UPDATE / DELETE
    // ============================================================
    public Parcela atualizar(Long id, Parcela dados) {
        Parcela existente = buscarPorId(id);

        existente.setNumeroParcela(dados.getNumeroParcela());
        existente.setDataVencimento(dados.getDataVencimento());
        existente.setValorPrevisto(dados.getValorPrevisto());
        existente.setObservacoes(dados.getObservacoes());

        return parcelaRepository.save(existente);
    }

    public void deletar(Long id) {
        parcelaRepository.delete(buscarPorId(id));
    }

    // ============================================================
    // GERAR PARCELAS AUTOMATICAMENTE
    // ============================================================
    @Transactional
    public void gerarParcelasParaEmprestimo(Emprestimo emprestimo) {

        Integer qtdParcelas = emprestimo.getQuantidadeParcelas();
        BigDecimal valorPrincipal = emprestimo.getValorPrincipal();

        LocalDate dataBase = emprestimo.getDataPrimeiroVencimento();

        BigDecimal valorParcela = valorPrincipal
                .divide(BigDecimal.valueOf(qtdParcelas), 2, RoundingMode.HALF_UP);

        // evita duplicar parcelas
        if (!parcelaRepository.findByEmprestimoId(emprestimo.getId()).isEmpty()) {
            return;
        }

        for (int n = 1; n <= qtdParcelas; n++) {
            Parcela parcela = Parcela.builder()
                    .emprestimo(emprestimo)
                    .numeroParcela(n)
                    .dataVencimento(dataBase.plusMonths(n - 1))
                    .valorPrevisto(valorParcela)
                    .status(ParcelaStatus.PENDENTE)
                    .build();

            parcelaRepository.save(parcela);
        }
    }
}
