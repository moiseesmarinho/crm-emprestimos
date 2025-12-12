package br.com.crm.crmemprestimos.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "parcelas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // número da parcela (1, 2, 3…)
    @Column(nullable = false)
    private Integer numeroParcela;

    // data de vencimento da parcela
    @Column(nullable = false)
    private LocalDate dataVencimento;

    // valor previsto para essa parcela
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorPrevisto;

    // valor que foi efetivamente pago (pode ser nulo se ainda não pagou)
    @Column(precision = 15, scale = 2)
    private BigDecimal valorPago;

    // data em que o pagamento foi feito
    private LocalDate dataPagamento;

    // status (PENDENTE, PAGA, EM_ATRASO, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParcelaStatus status;

    @ManyToOne
    @JoinColumn(name = "emprestimo_id", nullable = false)
    private Emprestimo emprestimo;

    @Column(length = 500)
    private String observacoes;

    // valor padrão antes de salvar no banco
    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = ParcelaStatus.PENDENTE;
        }
    }

    private Boolean paga = false;

    public Boolean getPaga() {
    return paga;
    }

    public void setPaga(Boolean paga) {
        this.paga = paga;
    }

}

