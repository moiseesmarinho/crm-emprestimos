package br.com.crm.crmemprestimos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RenegociacaoEmprestimoRequest {

    @NotNull(message = "Valor principal é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor principal deve ser maior que zero")
    private BigDecimal novoValorPrincipal;
    
    @NotNull(message = "Taxa de juros é obrigatória")
    @DecimalMin(value = "0.0", message = "Taxa de juros não pode ser negativa")
    private BigDecimal novaTaxaJurosMensal;
    
    @NotNull(message = "Quantidade de parcelas é obrigatória")
    @Min(value = 1, message = "Deve ter pelo menos 1 parcela")
    private Integer novaQuantidadeParcelas;
    
    @NotNull(message = "Data de vencimento é obrigatória")
    @FutureOrPresent(message = "Data de vencimento deve ser hoje ou no futuro")
    private LocalDate novaDataPrimeiroVencimento;
    
    private String motivo;

    public BigDecimal getNovoValorPrincipal() {
        return novoValorPrincipal;
    }

    public void setNovoValorPrincipal(BigDecimal novoValorPrincipal) {
        this.novoValorPrincipal = novoValorPrincipal;
    }

    public BigDecimal getNovaTaxaJurosMensal() {
        return novaTaxaJurosMensal;
    }

    public void setNovaTaxaJurosMensal(BigDecimal novaTaxaJurosMensal) {
        this.novaTaxaJurosMensal = novaTaxaJurosMensal;
    }

    public Integer getNovaQuantidadeParcelas() {
        return novaQuantidadeParcelas;
    }

    public void setNovaQuantidadeParcelas(Integer novaQuantidadeParcelas) {
        this.novaQuantidadeParcelas = novaQuantidadeParcelas;
    }

    public LocalDate getNovaDataPrimeiroVencimento() {
        return novaDataPrimeiroVencimento;
    }

    public void setNovaDataPrimeiroVencimento(LocalDate novaDataPrimeiroVencimento) {
        this.novaDataPrimeiroVencimento = novaDataPrimeiroVencimento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
