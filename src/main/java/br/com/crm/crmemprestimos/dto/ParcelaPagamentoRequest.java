package br.com.crm.crmemprestimos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ParcelaPagamentoRequest {

    private BigDecimal valorPago;
    private LocalDate dataPagamento;
    private String observacoes;

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
