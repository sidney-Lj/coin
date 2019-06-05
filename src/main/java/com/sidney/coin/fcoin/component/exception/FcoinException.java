package com.sidney.coin.fcoin.component.exception;


import com.sidney.coin.exception.BizException;

public class FcoinException extends BizException {
    private static final long serialVersionUID = -5474381300500884539L;

    public FcoinException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

}
