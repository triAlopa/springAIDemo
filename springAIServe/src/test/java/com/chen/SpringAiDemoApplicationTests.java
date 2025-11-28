package com.chen;

import com.chen.mapper.AIMessageMapper;
import com.chen.message.AIMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SpringAiDemoApplicationTests {


    @Autowired
    private AIMessageMapper aiMessageMapper;
    @Test
    void contextLoads() {
        List<AIMessage> messages = aiMessageMapper.getMessages("1");
        System.out.println(messages);
    }

}
