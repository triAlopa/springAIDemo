package com.chen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chen.exception.ModelBusinessException;
import com.chen.mapper.AIMessageMapper;
import com.chen.mapper.AISessionMapper;
import com.chen.mapper.ModelMapper;
import com.chen.pojo.dto.AISessionDTO;
import com.chen.pojo.entity.AIMessage;
import com.chen.pojo.entity.AISession;
import com.chen.pojo.entity.Model;
import com.chen.pojo.vo.AISessionVO;
import com.chen.service.SessionService;
import com.chen.util.CurrentUserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.chen.constant.ModelConstant.MODEL_NOTFOUND;
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


    @Override
    public List<AISessionVO> queryUserSession(Integer userId) {

        AISessionDTO sessionDTO = AISessionDTO.builder()
                .userId(userId)
                .isDel(NODEL)
                .build();

        List<AISession> list = sessionMapper.queryByUserId(sessionDTO);

        if (list == null) {
            //TODO 如果新用户可以加入一个默认会话提供给用户指导
            log.info("查找该:{}用户的会话列表为空", userId);
//            list= Collections.singletonList()
            return Collections.emptyList();
        }

        List<AISessionVO> voList = list.stream().map(
                item -> BeanUtil.copyProperties(item, AISessionVO.class)
        ).toList();

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
    }

    @Override
    public String createUserSession(AISessionDTO aiSessionDTO) {

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
            String modelId = String.valueOf(model.getModelId());
            aiSessionDTO.setModelId(modelId);
            //处理session的标题
            String name = model.getName();
            aiSessionDTO.setSessionTitle(name);
            //保存会话
            saveUserSession(aiSessionDTO);
            //保存第一条信息
            saveSessionFirstMessage(model, aiSessionDTO);
            //返回开场白
            return model.getOpenMessage();
        }
        List<String> userModelList = aiSessions.stream()
                .map(AISession::getModelId)
                .collect(Collectors.toList());

        Model model = new Model();
        Optional<Integer> first = models.stream()
                .map(Model::getModelId)
                //😅别指望你的同事 我一个人写类型都不可以保持一致，气笑了
                .filter(id -> !userModelList.contains(String.valueOf(id)))
                .findFirst();

        if (first.isPresent()) {
            first.ifPresent(id->handleModel(id, model));
        }
        //表示该用户model列表都使用了
        if(model.getModelId()==null){
            throw new ModelBusinessException(MODEL_NOTFOUND,NOT_FOUND);
        }
        aiSessionDTO.setModelId(String.valueOf(model.getModelId()));
        saveUserSession(aiSessionDTO);

        //首条信息的保存
        saveSessionFirstMessage(model, aiSessionDTO);

        return model.getOpenMessage();
    }






    /**
     * 查询hr列表 并赋值传递给形参
     * @param modelId
     * @param model
     */
    private void handleModel(Integer modelId,Model model) {
        Model queried= modelMapper.queryModelById(modelId);
        BeanUtil.copyProperties(queried, model);
    }

    /**
     * 保存用户创建的会话
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
     * @param model
     * @param aiSessionDTO
     */
    private void saveSessionFirstMessage(Model model,AISessionDTO aiSessionDTO) {

        AIMessage message = AIMessage.builder()
                .aiSessionId(aiSessionDTO.getSessionId())
                .type(MessageType.ASSISTANT)
                .contentType(TEXT_TYPE)
                .textContent(model.getOpenMessage())
                .creatorId(aiSessionDTO.getUserId())
                .createdTime(LocalDateTime.now())
                .lastTime(LocalDateTime.now())
                .build();

        messageMapper.insertSingleMessage(message);
    }

}
