package com.chen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.chen.exception.ModelBusinessException;
import com.chen.mapper.AIMessageMapper;
import com.chen.mapper.AISessionMapper;
import com.chen.mapper.ModelMapper;
import com.chen.pojo.dto.AISessionDTO;
import com.chen.pojo.entity.AIMessage;
import com.chen.pojo.entity.AISession;
import com.chen.pojo.entity.Model;
import com.chen.pojo.vo.AISessionVO;
import com.chen.pojo.vo.ModelVO;
import com.chen.service.ModelService;
import com.chen.service.SessionService;
import com.chen.util.CurrentUserHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.chen.constant.ModelConstant.MODEL_NOTFOUND;
import static com.chen.constant.RedisConstant.USER_CACHE_SESSION;
import static com.chen.constant.RedisConstant.USER_CACHE_SESSION_TTL;
import static com.chen.constant.ResultConstant.HTTPSTATUS.NOT_FOUND;

import static com.chen.constant.SystemConstant.CONTENT_TYPE.TEXT_TYPE;
import static com.chen.constant.UserConstant.*;

@Service
@Slf4j
public class SessionServiceImpl implements SessionService {

    @Autowired
    private AISessionMapper sessionMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AIMessageMapper messageMapper;

    @Autowired
    private ModelService modelService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    public List<AISessionVO> queryUserSession(Integer userId) {
        //缓存的key
        String key = USER_CACHE_SESSION + userId;

        String json = stringRedisTemplate.opsForValue().get(key);

        if (json != null&&!"[]".equals(json)) {
            log.info("已经为用户id{}缓存返回", userId);
            return (List<AISessionVO>) objectMapper.readValue(json, List.class);
        }


        AISessionDTO sessionDTO = AISessionDTO.builder()
                .userId(userId)
                .isDel(NODEL)
                .build();

        List<AISession> list = sessionMapper.queryByUserId(sessionDTO);

        if ( list.isEmpty()) {
            // 如果新用户可以加入一个默认会话提供给用户指导
            log.info("查找该:{}用户的会话列表为空", userId);
//            list= Collections.singletonList()
            sessionDTO.setUserId(DEFAULT_SESSION_USERID);
            return sessionMapper.queryByUserId(sessionDTO).stream().map(
                    item -> BeanUtil.copyProperties(item, AISessionVO.class)
            ).toList();
        }

        List<AISessionVO> voList = list.stream().map(
                item -> BeanUtil.copyProperties(item, AISessionVO.class)
        ).toList();
        //加入缓存

        try {
            String value = objectMapper.writeValueAsString(voList);
            stringRedisTemplate.opsForValue().set(
                    key,
                    value,//缓存雪崩
                    USER_CACHE_SESSION_TTL + RandomUtil.randomInt(1000 * 60),
                    TimeUnit.MILLISECONDS
            );
        } catch (JsonProcessingException e) {
            log.error("用户会话json化失败" + e.getMessage());
            throw new RuntimeException(e);
        }

        return voList;
    }

    @Override
    public void delUserSession(Integer userId, String sessionId) {

        AISessionDTO built = AISessionDTO.builder()
                .userId(userId)
                .isDel(ISDEL)
                .sessionId(sessionId)
                .build();

        //逻辑删除 会话即可
        sessionMapper.delSessionWithLogical(built);

        //缓存的key
        String key = USER_CACHE_SESSION + userId;
        //删除缓存
        stringRedisTemplate.delete(key);
    }

    @Override
    @Transactional
    public ModelVO createUserSession(AISessionDTO aiSessionDTO) {

        Integer userId = CurrentUserHolder.getCurrentUser().getId();
        //查询所有交流过的hr
        List<Model> models = modelMapper.queryAllModel();

        aiSessionDTO.setUserId(userId);
        aiSessionDTO.setEnable(ENABLE);
        aiSessionDTO.setIsDel(NODEL);

        //查询用户的所有未删除的会话
        List<AISession> aiSessions = sessionMapper.queryByUserId(aiSessionDTO);
        //会话为空,返回默认第一个hr即可
        if (aiSessions == null || aiSessions.isEmpty()) {
            Model model = models.get(0);
            //model_id
            String modelId = model.getModelId();
            aiSessionDTO.setModelId(modelId);
            //处理session的标题
            String name = model.getName();
            aiSessionDTO.setSessionTitle(name);
            //保存会话
            saveUserSession(aiSessionDTO);
            //保存信息
            saveSessionFirstMessage(model, aiSessionDTO, model.getDescription());
            //返回hr信息和公司信息

            return modelService.queryModelWithCompany(modelId);
        }
        //用户会话的hr的id
        List<String> userModelList = aiSessions.stream()
                .map(AISession::getModelId)
                .collect(Collectors.toList());

        Model model = new Model();
        Optional<String> first = models.stream()
                .map(Model::getModelId)
                //😅别指望你的同事 我一个人写类型都不可以保持一致，气笑了
                .filter(id -> !userModelList.contains(id))
                .findFirst();

        if (first.isPresent()) {
            first.ifPresent(id -> handleModel(id, model));
        }
        //表示该用户model列表都使用了
        String modelId = model.getModelId();
        if (modelId == null) {
            throw new ModelBusinessException(MODEL_NOTFOUND, NOT_FOUND);
        }
        //处理标题
        aiSessionDTO.setSessionTitle(model.getName());
        aiSessionDTO.setModelId(String.valueOf(model.getModelId()));
        saveUserSession(aiSessionDTO);

        //信息的保存
        saveSessionFirstMessage(model, aiSessionDTO, model.getDescription());

        //缓存的key
        String key = USER_CACHE_SESSION + userId;
        //删除缓存
        stringRedisTemplate.delete(key);

        return modelService.queryModelWithCompany(modelId);
    }


    /**
     * 查询hr列表 并赋值传递给形参
     *
     * @param modelId
     * @param model
     */
    private void handleModel(String modelId, Model model) {
        Model queried = modelMapper.queryModelById(modelId);
        BeanUtil.copyProperties(queried, model);
    }

    /**
     * 保存用户创建的会话
     *
     * @param aiSessionDTO
     */
    private void saveUserSession(AISessionDTO aiSessionDTO) {
        AISession session = AISession.builder()
                .sessionId(aiSessionDTO.getSessionId())
                .sessionTitle(aiSessionDTO.getSessionTitle())
                .userId(aiSessionDTO.getUserId())
                .isDel(NODEL)
                .enable(ENABLE)
                .modelId(aiSessionDTO.getModelId())
                .createdTime(LocalDateTime.now())
                .lastTime(LocalDateTime.now())
                .build();

        sessionMapper.insertSingleSession(session);
    }

    /**
     * 保创建会话AI自动返回的第一条信息
     * 以及交流场景信息 如hr的信息，公司信息
     *
     * @param model
     * @param aiSessionDTO
     * @param description
     */
    private void saveSessionFirstMessage(Model model, AISessionDTO aiSessionDTO, String description) {
        AIMessage promptMessage = AIMessage.builder()
                .aiSessionId(aiSessionDTO.getSessionId())
                .type(MessageType.SYSTEM)
                .contentType(TEXT_TYPE)
                .textContent(description)
                .creatorId(aiSessionDTO.getUserId())
                .createdTime(LocalDateTime.now())
                .lastTime(LocalDateTime.now())
                .build();

        AIMessage firstMessage = new AIMessage();
        BeanUtil.copyProperties(promptMessage, firstMessage);
        firstMessage.setType(MessageType.ASSISTANT);
        firstMessage.setTextContent(model.getOpenMessage());

        messageMapper.insertSingleMessage(promptMessage);
        messageMapper.insertSingleMessage(firstMessage);
    }

}
