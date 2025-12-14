package com.chen.service;

import com.chen.pojo.dto.AIMessageDTO;
import com.chen.pojo.dto.MessageContentDTO;
import com.chen.pojo.vo.AIMessageVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;

public interface MessageService {

    Flux<ServerSentEvent<String>> requestChat(MessageContentDTO content, HttpServletRequest request);

    List<AIMessageVO> queryUserMessages(String sessionId);

    void saveAIMessage(AIMessageDTO messageDTO);
}
