package com.chen.service.impl;

import com.chen.mapper.AIMessageMapper;
import com.chen.pojo.dto.MessageContentDTO;
import com.chen.pojo.Result;
import com.chen.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private AIMessageMapper messageMapper;


    private final ChatClient chatClient;

    public ChatServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Flux<ServerSentEvent<String>> requestChat(MessageContentDTO content) {

        return chatClient.prompt()
                .user(content.getPrompt())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, content.getChatId()))
                .stream()
                .content()
                .map(chunk -> ServerSentEvent.<String>builder().data(chunk).build());
    }

    @Override
    public Result getSessionMemory(String chatId) {
     /*   List<Message> messages = messageMapper.getMessages(chatId);

        if (messages == null || messages.isEmpty()) {
            return new Result("404", "没有数据", null);
        }*/


        return null;
    }
}
