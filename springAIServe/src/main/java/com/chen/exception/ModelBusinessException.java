package com.chen.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModelBusinessException extends RuntimeException {

    private String errorMsg;

    private int errorCode;

    public ModelBusinessException(String message) {
        super(message);
    }
}