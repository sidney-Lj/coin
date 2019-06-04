package com.sidney.fcoin.component.exception;

public class InitialKeyException extends RuntimeException {

    public InitialKeyException(String message) {
        super(message);
    }

    public InitialKeyException(String message, Throwable cause) {
        super(message, cause);
    }

}
