package com.sidney.coin.exception;

public class VerifyFailedException extends RuntimeException {
    private static final long serialVersionUID = -3760524526493913977L;

    public VerifyFailedException(String message) {
        super(message);
    }

    public VerifyFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
