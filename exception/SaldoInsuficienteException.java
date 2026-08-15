package br.com.MeuPlanner.exception;

public class SaldoInsuficienteException extends BusinessException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
}