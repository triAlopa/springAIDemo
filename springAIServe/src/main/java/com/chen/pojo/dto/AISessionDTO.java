package com.chen.pojo.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AISessionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private Integer userId;
    private String sessionTitle;
    private Integer enable;
    private Integer isDel;

}
