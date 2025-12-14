package com.chen.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AIMessageVO {

    private MessageType type;
    private String textContent;
    private String aiSessionId;
    private String contentType;
    @JsonFormat(pattern = "yy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}
