package com.chen.service;

import com.chen.pojo.MessageContentDTO;
import com.chen.pojo.Result;
import reactor.core.publisher.Flux;

public interface ChatService {
    Flux<String> requestChat(MessageContentDTO content);

    Result getSessionMemory(String chatId);
}
