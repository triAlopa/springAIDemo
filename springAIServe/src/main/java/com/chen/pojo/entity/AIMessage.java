package com.chen.pojo.entity;

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
public class AIMessage  {

    private Integer id;
    private MessageType type;
    private String contentType;
    private String textContent;
    private String medias;
    private String aiSessionId;
    private Integer creatorId;
    private String editorId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastTime;

    @AllArgsConstructor
    @Data
    public static class media{
        private String type;
        private String data;
    }
}
