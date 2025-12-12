package com.chen.controller;

import com.chen.pojo.dto.MessageContentDTO;
import com.chen.pojo.Result;
import com.chen.pojo.vo.AISessionVo;
import com.chen.service.ChatService;
import com.chen.util.CurrentUserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/user/ai")
@Slf4j
@Tag(name = "用户聊天接口")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Operation(description = "用户发出信息")
    @PostMapping(value = "/send", produces = "text/event-stream;charset=utf-8")
    public Flux<ServerSentEvent<String>> Send(@RequestBody MessageContentDTO content, HttpServletRequest request) {

        log.info("用户请求访问:{}", content);

        return chatService.requestChat(content, request);

    }

    @Operation(description = "用户查询会话")
    @GetMapping(value = "/getSession")
    public Result querySession(@RequestParam String chatId) {

        log.info("用户请求访问:{}的用户聊天记录", chatId);

        return chatService.getSessionMemory(chatId);
    }

    @Operation(summary = "获取用户会话信息", description = "根据前端请求头携带token,用户会话信息,返回前端展示")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "获取失败")
    })
    @GetMapping("/session")
    public Result<List<AISessionVo>> getUserSession() {
        Integer userId = CurrentUserHolder.getCurrentUser().getId();
        log.info("获取用户:{} 的会话信息", userId);

        List<AISessionVo> list = chatService.queryUserSession(userId);

        return Result.success(list);
    }
}
