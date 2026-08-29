package com.chen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.chen.mapper.AIMessageMapper;
import com.chen.mapper.AISessionMapper;
import com.chen.mapper.CompanyMapper;
import com.chen.mapper.UserMapper;
import com.chen.pojo.dto.AIMessageDTO;
import com.chen.pojo.dto.MessageContentDTO;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.AIMessage;
import com.chen.pojo.entity.Company;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.chen.config.ChatConfig.DEFAULT_PROMPT;
import static com.chen.constant.UserConstant.*;
import static org.springframework.ai.chat.messages.MessageType.ASSISTANT;
import static org.springframework.ai.chat.messages.MessageType.USER;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    private static final String SHOW_OFFER_CARD_PREFIX = "SHOW_OFFER_CARD:";

    @Autowired
    private AIMessageMapper messageMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private CompanyMapper companyMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final ChatClient chatClient;

    public MessageServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    @SneakyThrows
    public Flux<ServerSentEvent<String>> requestChat(MessageContentDTO content, HttpServletRequest request) {

        String userTokenName = jwtProperties.getUserTokenName();

        String userTokenId = request.getHeader(userTokenName);
        if (StrUtil.isBlank(userTokenId) || "null".equals(userTokenId)) {
            return unAuthThrow();
        }
        String token = stringRedisTemplate.opsForValue().get(userTokenId);


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
        Integer userId=user.getId();


        String userMessage = content.getPrompt();
        //校验用户的请求
        String response = "NO_OFFER_CARD";
        if (isPossibleOfferIntent(userMessage) && !shouldBlockOfferCard(userMessage)) {
            String checkOffer = """
                    你是一个“offer交互卡片触发器”，负责判断在当前语境下，是否应该向用户发送一段用于前端交互的offer HTML卡片。
                    只有当用户在当前语境中“提到offer并且有想看/想要/想确认/想决定/想接受offer的意向”时才触发。
                    用户这次说的话：%s

                    触发为“是”的典型语境（满足其一即可）：
                    1. 用户明确要offer：发offer/给我offer/下offer/出offer/把offer发我/来个offer/正式offer/录用通知
                    2. 用户想看offer内容或对offer做决定：offer长啥样/offer细节/确认offer/我接受offer/我同意/我签/我决定了
                    3. 用户在谈offer关键条款并希望落到offer上：offer薪资/offer待遇/offer条款（但不是纯闲聊）

                    不触发的典型语境：
                    1. 纯试探或玩笑：offer看看实力/随便发个offer看看/开玩笑/测试一下/演示一下
                    2. 只泛泛讨论机会或待遇，但没有落到offer意向：你们待遇咋样/薪资多少/诚意如何（未提offer或未表现要offer）

                    输出格式要求（必须严格遵守）：
                    - 不触发：只返回 NO_OFFER_CARD
                    - 触发：只返回 SHOW_OFFER_CARD:薪资
                    - 不允许任何解释、标点或其它内容
                    - “薪资”优先返回历史对话已谈妥的薪资；若没有明确谈妥，返回可给出的最高薪资
                    """.formatted(userMessage);

            response = chatClient.prompt(checkOffer)
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, content.getSessionId()))
                    .call().content();
        }

        final String offerDecision = response;
        return chatClient.prompt()
                .user(userMessage)
                .system(DEFAULT_PROMPT)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, content.getSessionId()))
                .stream()
                .content()
                .map(chunk -> ServerSentEvent.<String>builder().data(chunk).build())
                .doOnComplete(
                        ()-> {
                            //保存顺序
                            //1.先保存用户发出的请求
                            storeSuccessResponseUserMsg(content, USER, userId);
                            log.error("{}", offerDecision);
                            //2.判断offer
                            if(offerDecision != null && offerDecision.startsWith(SHOW_OFFER_CARD_PREFIX)) {
                                //处理信息并保存用户请求的offer
                                handleOfferMessage(content,offerDecision.substring(SHOW_OFFER_CARD_PREFIX.length()).trim(),userId);
                            }
                        }
                );
    }

    private boolean isPossibleOfferIntent(String userMessage) {
        if (StrUtil.isBlank(userMessage)) {
            return false;
        }
        String msg = userMessage.toLowerCase();
        return msg.contains("offer")
                || userMessage.contains("录用")
                || userMessage.contains("入职通知")
                || userMessage.contains("正式")
                || userMessage.contains("下发")
                || userMessage.contains("发我")
                || userMessage.contains("给我")
                || userMessage.contains("确认")
                || userMessage.contains("接受")
                || userMessage.contains("同意")
                || userMessage.contains("签")
                || userMessage.contains("条款");
    }

    private boolean shouldBlockOfferCard(String userMessage) {
        if (StrUtil.isBlank(userMessage)) {
            return false;
        }
        String msg = userMessage.toLowerCase();
        if (msg.contains("demo") || msg.contains("test")) {
            return true;
        }
        return userMessage.contains("看看实力")
                || userMessage.contains("随便")
                || userMessage.contains("开玩笑")
                || userMessage.contains("测试")
                || userMessage.contains("演示")
                || userMessage.contains("只是看看")
                || userMessage.contains("不是真的");
    }
    //保存offerHtml片段
    private void handleOfferMessage(MessageContentDTO content,String salary,Integer userId) {
        String sessionId = content.getSessionId();

        //salary jobTitle companyName
        Company company = companyMapper.selectCompanyBySessionId(sessionId);
        Context ctx=new Context();
        ctx.setVariable("salary",salary);
        ctx.setVariable("companyName",company.getName());
        ctx.setVariable("jobTitle",company.getJobTag());
        String process = templateEngine.process("generateOffer.html", ctx);

        MessageContentDTO message=new MessageContentDTO();
        message.setSessionId(sessionId);
        message.setPrompt(process);

        storeSuccessResponseUserMsg(message,ASSISTANT,userId);

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
