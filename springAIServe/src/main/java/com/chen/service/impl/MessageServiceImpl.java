package com.chen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.chen.mapper.AIMessageMapper;
import com.chen.mapper.AISessionMapper;
import com.chen.mapper.UserMapper;
import com.chen.pojo.dto.AIMessageDTO;
import com.chen.pojo.dto.MessageContentDTO;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.AIMessage;
import com.chen.pojo.entity.User;
import com.chen.pojo.properties.JwtProperties;
import com.chen.pojo.vo.AIMessageVO;
import com.chen.service.MessageService;
import com.chen.util.CurrentUserHolder;
import com.chen.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.chen.constant.UserConstant.*;
import static org.springframework.ai.chat.messages.MessageType.ASSISTANT;
import static org.springframework.ai.chat.messages.MessageType.USER;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private AIMessageMapper messageMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private AISessionMapper sessionMapper;

    @Autowired
    private JwtProperties jwtProperties;

    private final ChatClient chatClient;

    public MessageServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    @SneakyThrows
    public Flux<ServerSentEvent<String>> requestChat(MessageContentDTO content, HttpServletRequest request) {

        String userTokenName = jwtProperties.getUserTokenName();
        String token = request.getHeader(userTokenName);

        if (StrUtil.isBlank(token) || "null".equals(token)) {
            return unAuthThrow();
        }
        Jws<Claims> claims = null;
        try {
            claims = JwtUtil.parseUserToken(jwtProperties.getUserSignKey(), token);
        } catch (Exception e) {
            //token解析错误，伪装或者失效
            log.warn("用户token：{}解析错误", token);
            return unAuthThrow();
        }

        String userEmail = null;
        if (claims != null) {
            userEmail = claims.getPayload().get(EMAIL, String.class);
        }

        User user = userMapper.selectByEmail(userEmail);

        if (user == null) {
            return unAuthThrow();
        }
        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(user, userDTO, true);
        CurrentUserHolder.setCurrentUser(userDTO);

        return chatClient.prompt()
                .user(content.getPrompt())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, content.getSessionId()))
                .stream()
                .content()
                .map(chunk -> ServerSentEvent.<String>builder().data(chunk).build())
                .doOnComplete(
                        ()->storeSuccessResponseUserMsg(content,USER,user.getId())
                );
    }

    private void storeSuccessResponseUserMsg(MessageContentDTO content,MessageType messageType,Integer userId) {

        AIMessage message = AIMessage.builder()
                .aiSessionId(content.getSessionId())
                .type(messageType)
                .contentType("text")
                .textContent(content.getPrompt())
                .creatorId(userId)
                .createdTime(LocalDateTime.now())
                .lastTime(LocalDateTime.now())
                .build();

        messageMapper.insertSingleMessage(message);

    }


    private Flux<ServerSentEvent<String>> unAuthThrow() {
        return Flux.just(
                ServerSentEvent.<String>builder()
                        .event("error")
                        .data("{\"code\": 401, \"message\": \"请重新登录\"}")
                        .build()
        );
    }

    @Override
    public List<AIMessageVO> queryUserMessages(String sessionId) {
        List<AIMessage> messages = messageMapper.getMessages(sessionId);
        if (messages == null || messages.isEmpty()) {
            return Collections.emptyList();
        }

        List<AIMessageVO> list = messages.stream().filter(
                        //过滤系统提示词
                        item -> !MessageType.SYSTEM.equals(item.getType())
                ).sorted((v1, v2) -> v1.getCreatedTime().isBefore(v2.getCreatedTime()) ? -1 : 1)
                .map(item -> BeanUtil.copyProperties(item, AIMessageVO.class))
                .toList();

        return list;
    }

    @Override
    public void saveAIMessage(AIMessageDTO messageDTO) {
        MessageContentDTO contentDTO = new MessageContentDTO();
        contentDTO.setPrompt(messageDTO.getTextContent());
        contentDTO.setSessionId(messageDTO.getSessionId());

        storeSuccessResponseUserMsg(contentDTO,ASSISTANT,0);
    }
}
