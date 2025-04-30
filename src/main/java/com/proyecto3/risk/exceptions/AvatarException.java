package com.proyecto3.risk.exceptions;

public class AvatarException extends RuntimeException {
    public AvatarException(String message) {
        super(message);
    }

    public AvatarException(String message, Throwable cause) {
        super(message, cause);
    }

    public AvatarException(Throwable cause) {
        super(cause);
    }
}
