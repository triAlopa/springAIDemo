package com.chen.pojo.vo;


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
public class AISessionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String sessionId;
    private String sessionTitle;
    private String modelId;
    private Integer enable;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(pattern = "yyyy-MM-ddHH:mm:ss")
    private LocalDateTime lastTime;
}
