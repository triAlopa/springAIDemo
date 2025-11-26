package com.chen.controller;

import com.chen.pojo.MessageContentDTO;
import com.chen.pojo.Result;
import com.chen.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/user/ai")
@Slf4j
@Tag(name = "用户聊天接口")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Operation(description = "用户聊天")
    @PostMapping(value = "/chat", produces = "text/event-stream;charset=utf-8")
    public Flux<ServerSentEvent<String>> demo1( @RequestBody MessageContentDTO content) {

        log.info("用户请求访问:{}", content);

        return chatService.requestChat(content)
                .map(chunk -> ServerSentEvent.<String>builder().data(chunk).build());
    }

    @Operation(description = "用户聊天")
    @PostMapping(value = "/getSession", produces = "text/event-stream;charset=utf-8")
    public Result demo2( @RequestParam String chatId) {

        log.info("用户请求访问:{}的用户聊天记录", chatId);

        return chatService.getSessionMemory(chatId);
    }
}
