package com.chen.message;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * 根据MySQL实现持久化存储
 */
public class ChatMessageMemory implements ChatMemory {

    @Override
    public void add(String conversationId, List<Message> messages) {


    }

    @Override
    public List<Message> get(String conversationId) {
        return List.of();
    }

    @Override
    public void clear(String conversationId) {

    }


}
