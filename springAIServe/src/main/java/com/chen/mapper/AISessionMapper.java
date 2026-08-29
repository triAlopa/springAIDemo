package com.chen.mapper;

import com.chen.pojo.dto.AISessionDTO;
import com.chen.pojo.entity.AISession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AISessionMapper {

    List<AISession> queryByUserId(AISessionDTO sessionDTO);

    void delSessionWithLogical(AISessionDTO sessionDTO);

    void insertSingleSession(AISession session);

    void updateSingleSession(AISessionDTO sessionDTO);

    void batchUpdateSession(List<AISession> aiSessions);

    @Update("update tb_ai_session set feedback=#{rate} where user_id=#{userId} and session_id=#{sessionId}")
    void handleUserSessionRate(Integer userId, String sessionId, Integer rate);
}
