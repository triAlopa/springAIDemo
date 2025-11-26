package com.chen.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Select("select  * from spring_ai_chat_memory where conversation_id= #{chatId}")
    List<Message> getMessages(String chatId);

}
