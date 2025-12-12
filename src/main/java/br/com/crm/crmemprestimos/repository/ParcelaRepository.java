package br.com.crm.crmemprestimos.repository;

import br.com.crm.crmemprestimos.model.Emprestimo;
import br.com.crm.crmemprestimos.model.Parcela;
import br.com.crm.crmemprestimos.model.ParcelaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ParcelaRepository extends JpaRepository<Parcela, Long> {

    // ============================================================
    // LISTAGENS
    // ============================================================

    List<Parcela> findByEmprestimo(Emprestimo emprestimo);

    List<Parcela> findByEmprestimoId(Long emprestimoId);

    List<Parcela> findByEmprestimoIdOrderByNumeroParcelaAsc(Long emprestimoId);

    // ============================================================
    // BUSCAS ESPECÍFICAS
    // ============================================================

    // Buscar parcela pelo número dentro do empréstimo (usado no pagar por número)
    Optional<Parcela> findByEmprestimoIdAndNumeroParcela(Long emprestimoId, Integer numeroParcela);

    // Próxima parcela pendente (para o resumo)
    Optional<Parcela> findFirstByEmprestimoAndStatusAndDataVencimentoAfterOrderByDataVencimentoAsc(
            Emprestimo emprestimo,
            ParcelaStatus status,
            LocalDate data
    );

    // ============================================================
    // CONTAGENS / REGRAS
    // ============================================================

    long countByEmprestimoAndStatus(Emprestimo emprestimo, ParcelaStatus status);

    long countByEmprestimoIdAndStatus(Long emprestimoId, ParcelaStatus status);

    boolean existsByEmprestimoIdAndStatusNot(Long emprestimoId, ParcelaStatus status);

    // ============================================================
    // CONSULTAS ÚTEIS
    // ============================================================

    List<Parcela> findByStatus(ParcelaStatus status);

    List<Parcela> findByDataVencimentoBeforeAndStatus(LocalDate data, ParcelaStatus status);
}
