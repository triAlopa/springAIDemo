package com.chen.mapper;

import com.chen.pojo.dto.AISessionDTO;
import com.chen.pojo.entity.AIMessage;
import com.chen.pojo.entity.AISession;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AISessionMapper {

    List<AISession> queryByUserId(AISessionDTO sessionDTO);

}
