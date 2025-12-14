package com.chen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chen.mapper.AIMessageMapper;
import com.chen.mapper.AISessionMapper;
import com.chen.pojo.dto.AISessionDTO;
import com.chen.pojo.entity.AISession;
import com.chen.pojo.vo.AISessionVO;
import com.chen.service.SessionService;
import com.chen.util.CurrentUserHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.chen.constant.UserConstant.*;

@Service
@Slf4j
public class SessionServiceImpl implements SessionService {

    @Autowired
    private AISessionMapper sessionMapper;


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
    @Transactional(rollbackFor = Exception.class)
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
    public void createUserSession(AISessionDTO aiSessionDTO) {

        Integer userId = CurrentUserHolder.getCurrentUser().getId();

        AISession session = AISession.builder()
                .sessionId(aiSessionDTO.getSessionId())
                .sessionTitle(aiSessionDTO.getSessionTitle())
                .userId(userId)
                .isDel(NODEL)
                .enable(ENABLE)
                // TODO 加入人物模型id ...
                .createdTime(LocalDateTime.now())
                .lastTime(LocalDateTime.now())
                .build();

        sessionMapper.insertSingleSession(session);

    }


}
