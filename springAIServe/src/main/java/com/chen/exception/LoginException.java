package com.chen.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginException extends RuntimeException {

    private String errorMsg;

    private int errorCode;

    public LoginException(String message) {
        super(message);
    }
}