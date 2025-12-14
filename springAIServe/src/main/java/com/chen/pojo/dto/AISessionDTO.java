package com.chen.pojo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AISessionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private Integer userId;
    private String modelId;
    private String sessionTitle;
    private Integer enable;
    private Integer isDel;

}
