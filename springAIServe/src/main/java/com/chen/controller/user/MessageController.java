package com.chen.controller.user;

import com.chen.aspect.LogOperation;
import com.chen.pojo.dto.AIMessageDTO;
import com.chen.pojo.dto.MessageContentDTO;
import com.chen.pojo.Result;
import com.chen.pojo.vo.AIMessageVO;
import com.chen.service.MessageService;
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
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Operation(description = "用户发出信息")
    @PostMapping(value = "/send", produces = "text/event-stream;charset=utf-8")
    @LogOperation
    public Flux<ServerSentEvent<String>> Send(@RequestBody MessageContentDTO content, HttpServletRequest request) {

        log.info("用户请求访问:{}", content);

        return messageService.requestChat(content, request);

    }

    @Operation(summary = "获取用户某一会话的具体信息内容", description = "根据前端请求头携带token,查询用户会话信息,返回前端展示")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "业务错误")
    })
    @GetMapping("/message")
    @LogOperation
    public Result<List<AIMessageVO>> getUserMessages(@RequestParam String sessionId) {

        log.info("获取会话:{} 的消息体", sessionId);

        List<AIMessageVO> list = messageService.queryUserMessages(sessionId);

        return Result.success(list);
    }

    @Operation(description = "AI信息保存")
    @PostMapping(value = "/message/save")
    @LogOperation
    public Result saveAIMessage(@RequestBody AIMessageDTO messageDTO) {

        log.info("请求AI信息保存:{}", messageDTO);

        messageService.saveAIMessage(messageDTO);

        return Result.success();
    }
}
