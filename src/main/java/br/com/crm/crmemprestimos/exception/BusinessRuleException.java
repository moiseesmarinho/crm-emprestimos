package br.com.crm.crmemprestimos.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String msg) { super(msg); }
}