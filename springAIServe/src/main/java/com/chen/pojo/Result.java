package com.chen.pojo;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private String code;
    private String msg;
    private Object data;

    public static Result success(Object data) {
        return new Result("200", "success", data);
    }

}
