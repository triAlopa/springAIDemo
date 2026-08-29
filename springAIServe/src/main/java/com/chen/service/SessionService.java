package com.chen.service;

import com.chen.pojo.dto.AISessionDTO;
import com.chen.pojo.vo.AIMessageVO;
import com.chen.pojo.vo.AISessionVO;
import com.chen.pojo.vo.ModelVO;

import java.util.List;

public interface SessionService {

    /**
     * 查询用户会话
     * @param userId
     * @return
     */
    List<AISessionVO> queryUserSession(Integer userId);


    /**
     * 删除用户会话
     * @param userId
     * @param sessionId
     */
    void delUserSession(Integer userId, String sessionId);

    /**
     * 创建用户会话
     * @param sessionDTO
     */
    ModelVO createUserSession(AISessionDTO sessionDTO);

    /**
     * 用户打分
     * @param sessionId
     * @param rate
     */
    void responseSessionRate(String sessionId, Integer rate);
}
