package com.chen.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIMessageDTO  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String type;
    private String contentType;
    private String textContent;
}
