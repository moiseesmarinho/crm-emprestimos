package br.com.crm.crmemprestimos.service;

import br.com.crm.crmemprestimos.dto.EncerramentoEmprestimoRequest;
import br.com.crm.crmemprestimos.dto.EmprestimoResumoResponse;
import br.com.crm.crmemprestimos.exception.BusinessRuleException;
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
import java.time.LocalDate;
import java.util.List;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final ParcelaRepository parcelaRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository,
                             ParcelaRepository parcelaRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.parcelaRepository = parcelaRepository;
    }

    // ============================================================
    // LISTAR / BUSCAR
    // ============================================================
    public List<Emprestimo> listarTodos() {
        return emprestimoRepository.findAll();
    }

    public Emprestimo buscarPorId(Long id) {
        return emprestimoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Empréstimo não encontrado. ID: " + id));
    }

    // ============================================================
    // SALVAR (CREATE/UPDATE)
    // ============================================================
    @Transactional
    public Emprestimo salvar(Emprestimo emprestimo) {
        if (emprestimo == null) {
            throw new IllegalArgumentException("O empréstimo não pode ser nulo");
        }

        if (emprestimo.getStatus() == null) {
            emprestimo.setStatus(StatusEmprestimo.ATIVO);
        }

        if (emprestimo.getDataContratacao() == null) {
            emprestimo.setDataContratacao(LocalDate.now());
        }

        if (emprestimo.getSaldoDevedor() == null && emprestimo.getValorPrincipal() != null) {
            emprestimo.setSaldoDevedor(emprestimo.getValorPrincipal());
        }

        // Padroniza quantidadeParcelas / numeroParcelas (pra não dar confusão no resto do projeto)
        if (emprestimo.getQuantidadeParcelas() != null) {
            emprestimo.setNumeroParcelas(emprestimo.getQuantidadeParcelas());
        } else if (emprestimo.getNumeroParcelas() != null) {
            emprestimo.setQuantidadeParcelas(emprestimo.getNumeroParcelas());
        } else {
            throw new BusinessRuleException("O número de parcelas é obrigatório");
        }

        validarEmprestimo(emprestimo);

        return emprestimoRepository.save(emprestimo);
    }

    private void validarEmprestimo(Emprestimo emprestimo) {
        if (emprestimo.getValorPrincipal() == null || emprestimo.getValorPrincipal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("O valor principal deve ser maior que zero");
        }

        if (emprestimo.getQuantidadeParcelas() == null || emprestimo.getQuantidadeParcelas() <= 0) {
            throw new BusinessRuleException("A quantidade de parcelas deve ser maior que zero");
        }

        if (emprestimo.getDataPrimeiroVencimento() == null) {
            throw new BusinessRuleException("A data do primeiro vencimento é obrigatória");
        }

        if (emprestimo.getDataContratacao() != null &&
                emprestimo.getDataPrimeiroVencimento().isBefore(emprestimo.getDataContratacao())) {
            throw new BusinessRuleException("A data do primeiro vencimento não pode ser anterior à data de contratação");
        }
    }

    // ============================================================
    // ATUALIZAR / DELETAR
    // ============================================================
    public Emprestimo atualizar(Long id, Emprestimo dadosAtualizados) {
        Emprestimo existente = buscarPorId(id);

        existente.setValorPrincipal(dadosAtualizados.getValorPrincipal());
        existente.setTaxaJurosMensal(dadosAtualizados.getTaxaJurosMensal());
        existente.setQuantidadeParcelas(dadosAtualizados.getQuantidadeParcelas());
        existente.setNumeroParcelas(dadosAtualizados.getNumeroParcelas());
        existente.setDataPrimeiroVencimento(dadosAtualizados.getDataPrimeiroVencimento());
        existente.setObservacoes(dadosAtualizados.getObservacoes());

        if (dadosAtualizados.getStatus() != null) {
            existente.setStatus(dadosAtualizados.getStatus());
        }
        if (dadosAtualizados.getSaldoDevedor() != null) {
            existente.setSaldoDevedor(dadosAtualizados.getSaldoDevedor());
        }
        if (dadosAtualizados.getDataEncerramento() != null) {
            existente.setDataEncerramento(dadosAtualizados.getDataEncerramento());
        }
        if (dadosAtualizados.getMotivoEncerramento() != null) {
            existente.setMotivoEncerramento(dadosAtualizados.getMotivoEncerramento());
        }

        // re-padroniza se vier bagunçado no PUT
        if (existente.getQuantidadeParcelas() != null) {
            existente.setNumeroParcelas(existente.getQuantidadeParcelas());
        } else if (existente.getNumeroParcelas() != null) {
            existente.setQuantidadeParcelas(existente.getNumeroParcelas());
        }

        return emprestimoRepository.save(existente);
    }

    public void deletar(Long id) {
        Emprestimo existente = buscarPorId(id);
        emprestimoRepository.delete(existente);
    }

    // ============================================================
    // MÉTODOS ESPECIAIS
    // ============================================================
    public Emprestimo quitarEmprestimo(Long id, String motivo, LocalDate dataEncerramento) {
        Emprestimo emp = buscarPorId(id);

        if (emp.getStatus() == StatusEmprestimo.QUITADO) {
            throw new RegraNegocioException("Empréstimo já está quitado.");
        }
        if (emp.getStatus() == StatusEmprestimo.CANCELADO) {
            throw new RegraNegocioException("Não é possível quitar um empréstimo cancelado.");
        }

        emp.setStatus(StatusEmprestimo.QUITADO);
        emp.setSaldoDevedor(BigDecimal.ZERO);
        emp.setDataEncerramento(dataEncerramento != null ? dataEncerramento : LocalDate.now());
        emp.setMotivoEncerramento(motivo != null ? motivo : "Quitação do contrato");

        return emprestimoRepository.save(emp);
    }

    public Emprestimo cancelarEmprestimo(Long id, String motivo, LocalDate dataEncerramento) {
        Emprestimo emp = buscarPorId(id);

        if (emp.getStatus() == StatusEmprestimo.QUITADO) {
            throw new RegraNegocioException("Não é possível cancelar um empréstimo quitado.");
        }
        if (emp.getStatus() == StatusEmprestimo.CANCELADO) {
            throw new RegraNegocioException("Empréstimo já está cancelado.");
        }

        emp.setStatus(StatusEmprestimo.CANCELADO);
        emp.setDataEncerramento(dataEncerramento != null ? dataEncerramento : LocalDate.now());
        emp.setMotivoEncerramento(motivo != null ? motivo : "Cancelamento do contrato");

        return emprestimoRepository.save(emp);
    }

    public Emprestimo marcarAtrasado(Long id) {
        Emprestimo emp = buscarPorId(id);

        if (emp.getStatus() == StatusEmprestimo.QUITADO || emp.getStatus() == StatusEmprestimo.CANCELADO) {
            throw new RegraNegocioException("Não é possível marcar como atrasado um empréstimo encerrado.");
        }

        emp.setStatus(StatusEmprestimo.ATRASADO);
        return emprestimoRepository.save(emp);
    }

    public Emprestimo renegociarEmprestimo(
            Long id,
            BigDecimal novoValorPrincipal,
            BigDecimal novaTaxaJurosMensal,
            Integer novaQuantidadeParcelas,
            LocalDate novaDataPrimeiroVencimento,
            String motivo
    ) {
        if (novoValorPrincipal == null || novoValorPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Valor principal inválido");
        }
        if (novaTaxaJurosMensal == null || novaTaxaJurosMensal.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("Taxa de juros inválida");
        }
        if (novaQuantidadeParcelas == null || novaQuantidadeParcelas <= 0) {
            throw new RegraNegocioException("Quantidade de parcelas inválida");
        }
        if (novaDataPrimeiroVencimento == null) {
            throw new RegraNegocioException("Data de vencimento inválida");
        }

        Emprestimo emp = buscarPorId(id);

        if (emp.getStatus() == StatusEmprestimo.CANCELADO) {
            throw new RegraNegocioException("Não é possível renegociar um empréstimo cancelado.");
        }
        if (emp.getStatus() == StatusEmprestimo.QUITADO) {
            throw new RegraNegocioException("Não é possível renegociar um empréstimo já quitado.");
        }

        emp.setValorPrincipal(novoValorPrincipal);
        emp.setTaxaJurosMensal(novaTaxaJurosMensal);
        emp.setQuantidadeParcelas(novaQuantidadeParcelas);
        emp.setNumeroParcelas(novaQuantidadeParcelas);
        emp.setDataPrimeiroVencimento(novaDataPrimeiroVencimento);
        emp.setSaldoDevedor(novoValorPrincipal);
        emp.setStatus(StatusEmprestimo.RENEGOCIADO);
        emp.setDataEncerramento(null);
        emp.setMotivoEncerramento(motivo != null ? motivo : "Contrato renegociado");

        return emprestimoRepository.save(emp);
    }

    // ============================================================
    // RESUMO DO EMPRÉSTIMO (usando o ParcelaRepository atualizado)
    // ============================================================
    public EmprestimoResumoResponse resumoDoEmprestimo(Long id) {
        Emprestimo emprestimo = buscarPorId(id);

        long pagas = parcelaRepository.countByEmprestimoAndStatus(emprestimo, ParcelaStatus.PAGA);
        long atrasadas = parcelaRepository.countByEmprestimoAndStatus(emprestimo, ParcelaStatus.ATRASADA);
        long pendentes = parcelaRepository.countByEmprestimoAndStatus(emprestimo, ParcelaStatus.PENDENTE);

        LocalDate hoje = LocalDate.now();
        Parcela proxima = parcelaRepository
                .findFirstByEmprestimoAndStatusAndDataVencimentoAfterOrderByDataVencimentoAsc(
                        emprestimo, ParcelaStatus.PENDENTE, hoje
                )
                .orElse(null);

        EmprestimoResumoResponse dto = new EmprestimoResumoResponse();
        dto.setEmprestimoId(emprestimo.getId());
        dto.setClienteId(emprestimo.getCliente().getId());
        dto.setNomeCliente(emprestimo.getCliente().getNomeCompleto());

        dto.setValorPrincipal(emprestimo.getValorPrincipal());
        dto.setSaldoDevedor(emprestimo.getSaldoDevedor());
        dto.setStatus(emprestimo.getStatus().name());

        dto.setTotalParcelas(emprestimo.getQuantidadeParcelas());
        dto.setQtdParcelasPagas(pagas);
        dto.setQtdParcelasEmAtraso(atrasadas);
        dto.setQtdParcelasPendentes(pendentes);

        dto.setDataPrimeiroVencimento(emprestimo.getDataPrimeiroVencimento());
        dto.setDataProximoVencimento(proxima != null ? proxima.getDataVencimento() : null);

        return dto;
    }

    // ============================================================
    // ENCERRAR (endpoint genérico de encerramento)
    // ============================================================
    @Transactional
    public void encerrarEmprestimo(Long emprestimoId, EncerramentoEmprestimoRequest request) {
        Emprestimo emprestimo = buscarPorId(emprestimoId);

        if (emprestimo.getStatus() == StatusEmprestimo.CANCELADO || emprestimo.getStatus() == StatusEmprestimo.QUITADO) {
            throw new BusinessRuleException("Este empréstimo já está encerrado.");
        }

        if (request != null && request.getMotivoEncerramento() != null && !request.getMotivoEncerramento().isBlank()) {
            emprestimo.setMotivoEncerramento(request.getMotivoEncerramento());
        } else {
            emprestimo.setMotivoEncerramento("Encerramento do contrato");
        }

        emprestimo.setStatus(StatusEmprestimo.CANCELADO);
        emprestimo.setDataEncerramento(
                request != null && request.getDataEncerramento() != null
                        ? request.getDataEncerramento()
                        : LocalDate.now()
        );

        emprestimoRepository.save(emprestimo);
    }
}
