package com.chen.mapper;

import com.chen.pojo.entity.AIMessage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AIMessageMapper {



//    @Select("select * from tb_ai_message where ai_session_id=#{sessionId}")
    List<AIMessage> getMessages(String sessionId);

    @Insert("insert into tb_ai_message(type, content_type, text_content," +
            " ai_session_id, creator_id, created_time, last_time)" +
            "values (#{type},#{contentType},#{textContent},#{aiSessionId}," +
            "#{creatorId},#{createdTime},#{lastTime})")
    void insertSingleMessage(AIMessage message);


    //void delMessageWithSessionId(String sessionId);
}
