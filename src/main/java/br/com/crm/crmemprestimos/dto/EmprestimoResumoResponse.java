package br.com.crm.crmemprestimos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmprestimoResumoResponse {

    private Long emprestimoId;
    private Long clienteId;
    private String nomeCliente;

    private BigDecimal valorPrincipal;
    private BigDecimal saldoDevedor;
    private String status;

    private Integer totalParcelas;
    private Long qtdParcelasPagas;
    private Long qtdParcelasEmAtraso;
    private Long qtdParcelasPendentes;

    private LocalDate dataPrimeiroVencimento;
    private LocalDate dataProximoVencimento;

    // GETTERS E SETTERS

    public Long getEmprestimoId() {
        return emprestimoId;
    }

    public void setEmprestimoId(Long emprestimoId) {
        this.emprestimoId = emprestimoId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public BigDecimal getValorPrincipal() {
        return valorPrincipal;
    }

    public void setValorPrincipal(BigDecimal valorPrincipal) {
        this.valorPrincipal = valorPrincipal;
    }

    public BigDecimal getSaldoDevedor() {
        return saldoDevedor;
    }

    public void setSaldoDevedor(BigDecimal saldoDevedor) {
        this.saldoDevedor = saldoDevedor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalParcelas() {
        return totalParcelas;
    }

    public void setTotalParcelas(Integer totalParcelas) {
        this.totalParcelas = totalParcelas;
    }

    public Long getQtdParcelasPagas() {
        return qtdParcelasPagas;
    }

    public void setQtdParcelasPagas(Long qtdParcelasPagas) {
        this.qtdParcelasPagas = qtdParcelasPagas;
    }

    public Long getQtdParcelasEmAtraso() {
        return qtdParcelasEmAtraso;
    }

    public void setQtdParcelasEmAtraso(Long qtdParcelasEmAtraso) {
        this.qtdParcelasEmAtraso = qtdParcelasEmAtraso;
    }

    public Long getQtdParcelasPendentes() {
        return qtdParcelasPendentes;
    }

    public void setQtdParcelasPendentes(Long qtdParcelasPendentes) {
        this.qtdParcelasPendentes = qtdParcelasPendentes;
    }

    public LocalDate getDataPrimeiroVencimento() {
        return dataPrimeiroVencimento;
    }

    public void setDataPrimeiroVencimento(LocalDate dataPrimeiroVencimento) {
        this.dataPrimeiroVencimento = dataPrimeiroVencimento;
    }

    public LocalDate getDataProximoVencimento() {
        return dataProximoVencimento;
    }

    public void setDataProximoVencimento(LocalDate dataProximoVencimento) {
        this.dataProximoVencimento = dataProximoVencimento;
    }
}

