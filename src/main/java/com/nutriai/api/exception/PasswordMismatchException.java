package com.nutriai.api.exception;


/**
 * Exceção lançada quando a senha e a confirmação de senha não coincidem durante o registro.
 */
public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
