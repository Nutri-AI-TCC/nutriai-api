package com.nutriai.api.exception;

public class CpfCnpjAlreadyExistsException extends RuntimeException {
    public CpfCnpjAlreadyExistsException(String message) {
        super(message);
    }
}
