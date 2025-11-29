package com.chen.message;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.chen.mapper.AIMessageMapper;
import com.chen.pojo.entity.AIMessage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.content.Media;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.*;

/**
 * 根据MySQL实现持久化存储
 */
@Component
public class ChatMessageMemory implements ChatMemory {

    @Resource
    private AIMessageMapper aiMessageMapper;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 由用户发出请求后自行加入自己创建的表
     * 此法故不实现
     *
     * @param conversationId
     * @param messages
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
    }

    @Override
    public List<Message> get(String conversationId) {
        List<AIMessage> messages = aiMessageMapper.getMessages(conversationId);
        return messages.stream().map(this::toSpringAIMessage).toList();
    }

    /**
     * 将我们的aiMessage实体转为springAI接口的org.springframework.ai.chat.messages.Message
     * @param aiMessage
     * @return
     */
    @SneakyThrows
    private Message toSpringAIMessage(AIMessage aiMessage) {
        List<Media> mediaList = new ArrayList<>();
        List<AIMessage.media> medias = new ArrayList<>();

        //获取用户可能发的媒体信息
        String messageMedias = aiMessage.getMedias();
        if(StrUtil.isNotBlank(messageMedias)) {
            medias=objectMapper.readValue(messageMedias, new TypeReference<List<AIMessage.media>>() {});
        }

        if (!CollectionUtil.isEmpty(medias)) {
            mediaList = medias.stream().map(this::toSpringMedia).toList();
        }

        //用户信息
        if (MessageType.USER.equals(aiMessage.getType())) {
            return UserMessage.builder()
                    .text(aiMessage.getTextContent())
                    .media(mediaList)
                    .build();
        }
        //ai信息
        if (MessageType.ASSISTANT.equals(aiMessage.getType())) {
            return new AssistantMessage(aiMessage.getTextContent());
        }

        if (MessageType.SYSTEM.equals(aiMessage.getType())) {
            return new AssistantMessage(aiMessage.getTextContent());
        }

        throw new IllegalArgumentException("不支持的消息类型");
    }


    @SneakyThrows
    private Media toSpringMedia(AIMessage.media media) {
        return new Media(MediaType.valueOf(media.getType()), new URI(media.getData()));
    }

    @Override
    public void clear(String conversationId) {

    }
}
