package com.chen.exception;

import lombok.Data;

@Data
public class AccountBusinessException extends RuntimeException{
    private Integer errorCode;
    private String errorMsg;

    public AccountBusinessException(String message) {
        super(message);
    }
    public AccountBusinessException(String message, Integer errorCode) {
        this.errorCode = errorCode;
        this.errorMsg = message;
    }

}
