package com.sidney.coin.exception;

public class SignatureFailedException extends RuntimeException{

    public SignatureFailedException(String message) {
        super(message);
    }

    public SignatureFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
