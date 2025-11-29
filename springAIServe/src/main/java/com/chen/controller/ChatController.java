package com.chen.controller;

import com.chen.pojo.dto.MessageContentDTO;
import com.chen.pojo.Result;
import com.chen.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/user/ai")
@Slf4j
@Tag(name = "用户聊天接口")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Operation(description = "用户发出信息")
    @PostMapping(value = "/send", produces = "text/event-stream;charset=utf-8")
    public Flux<ServerSentEvent<String>> Send( @RequestBody MessageContentDTO content) {

        log.info("用户请求访问:{}", content);

        return chatService.requestChat(content);

    }

    @Operation(description = "用户查询会话")
    @GetMapping(value = "/getSession")
    public Result querySession(@RequestParam String chatId) {

        log.info("用户请求访问:{}的用户聊天记录", chatId);

        return chatService.getSessionMemory(chatId);
    }
}
