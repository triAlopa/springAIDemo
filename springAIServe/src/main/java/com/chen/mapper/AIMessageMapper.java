package com.chen.mapper;

import com.chen.pojo.entity.AIMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AIMessageMapper {



//    @Select("select * from tb_ai_message where ai_session_id=#{sessionId}")
    List<AIMessage> getMessages(String sessionId);

}
