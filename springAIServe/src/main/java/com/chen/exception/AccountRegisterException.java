package com.chen.exception;

import lombok.Data;

@Data
public class AccountRegisterException extends RuntimeException {
    private Integer errorCode;
    private String errorMsg;
    public AccountRegisterException(String message) {
        super(message);
    }

    public AccountRegisterException(String message, Integer errorCode) {
        this.errorCode = errorCode;
        this.errorMsg = message;
    }
}
