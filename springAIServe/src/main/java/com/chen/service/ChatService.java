package com.chen.service;

import com.chen.pojo.dto.MessageContentDTO;
import com.chen.pojo.Result;
import com.chen.pojo.vo.AISessionVo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    Flux<ServerSentEvent<String>> requestChat(MessageContentDTO content, HttpServletRequest request);

    Result getSessionMemory(String chatId);

    List<AISessionVo> queryUserSession(Integer userId);
}
