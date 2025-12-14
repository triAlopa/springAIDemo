package com.chen.pojo.entity;


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
public class AISession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String sessionId;
    private String sessionTitle;
    private Integer userId;
    private String modelId;
    private Integer enable;
    private Integer feedback;
    private Integer isDel;

    @JsonFormat(pattern = "yyyy-dd-MM HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(pattern = "yyyy-dd-MM HH:mm:ss")
    private LocalDateTime lastTime;
}
