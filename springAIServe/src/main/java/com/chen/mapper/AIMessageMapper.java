package com.chen.mapper;

import com.chen.message.AIMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.ai.chat.messages.Message;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AIMessageMapper {



//    @Select("select * from tb_ai_message where ai_session_id=#{sessionId}")
    List<AIMessage> getMessages(String sessionId);

}
