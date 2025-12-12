package com.chen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.chen.exception.LoginException;
import com.chen.mapper.AIMessageMapper;
import com.chen.mapper.AISessionMapper;
import com.chen.mapper.UserMapper;
import com.chen.pojo.dto.AISessionDTO;
import com.chen.pojo.dto.MessageContentDTO;
import com.chen.pojo.Result;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.AIMessage;
import com.chen.pojo.entity.AISession;
import com.chen.pojo.entity.User;
import com.chen.pojo.properties.JwtProperties;
import com.chen.pojo.vo.AISessionVo;
import com.chen.service.ChatService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

import static com.chen.constant.ResultConstant.HTTPSTATUS.SUCCESS;
import static com.chen.constant.ResultConstant.HTTPSTATUS.UNAUTHORIZED;
import static com.chen.constant.ResultConstant.UNAUTHMSG;
import static com.chen.constant.UserConstant.*;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private AIMessageMapper messageMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private AISessionMapper sessionMapper;

    @Autowired
    private JwtProperties jwtProperties;

    private final ChatClient chatClient;

    public ChatServiceImpl(ChatClient chatClient) {
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
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, content.getChatId()))
                .stream()
                .content()
                .map(chunk -> ServerSentEvent.<String>builder().data(chunk).build());
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
    public Result getSessionMemory(String chatId) {
        List<AIMessage> messages = messageMapper.getMessages(chatId);



        return messages.isEmpty()?Result.fil("没有数据",SUCCESS):Result.success(messages);
    }

    @Override
    public List<AISessionVo> queryUserSession(Integer userId) {

        AISessionDTO sessionDTO = AISessionDTO.builder()
                .userId(userId)
                .isDel(NODEL)
                .build();

        List<AISession> list = sessionMapper.queryByUserId(sessionDTO);

        if(list==null){
            //TODO 如果新用户可以加入一个默认会话提供给用户指导
            log.info("查找该:{}用户的会话列表为空",userId);
//            list= Collections.singletonList()
            return Collections.emptyList();
        }

        List<AISessionVo> voList = list.stream().map(
                item -> BeanUtil.copyProperties(item, AISessionVo.class)
        ).toList();

        return voList;
    }
}
