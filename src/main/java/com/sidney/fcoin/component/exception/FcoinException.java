package com.sidney.fcoin.component.exception;


public class FcoinException extends BizException {
    private static final long serialVersionUID = -5474381300500884539L;

    public FcoinException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

}
