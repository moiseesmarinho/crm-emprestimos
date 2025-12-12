package br.com.crm.crmemprestimos.dto;

import java.time.LocalDate;

public class EncerramentoEmprestimoRequest {

    private String motivoEncerramento;
    private LocalDate dataEncerramento; // opcional

    public String getMotivoEncerramento() {
        return motivoEncerramento;
    }

    public void setMotivoEncerramento(String motivoEncerramento) {
        this.motivoEncerramento = motivoEncerramento;
    }

    public LocalDate getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(LocalDate dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }
}
