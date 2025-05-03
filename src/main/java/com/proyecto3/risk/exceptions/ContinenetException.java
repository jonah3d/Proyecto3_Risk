package com.proyecto3.risk.exceptions;

public class ContinenetException extends RuntimeException {
    public ContinenetException(String message) {
        super(message);
    }

    public ContinenetException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContinenetException(Throwable cause) {
        super(cause);
    }
}
