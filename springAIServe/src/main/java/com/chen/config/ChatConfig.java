package com.chen.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.ai.chat.memory.repository.jdbc.MysqlChatMemoryRepositoryDialect;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ChatConfig {

    @Bean
    public JdbcChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        JdbcChatMemoryRepositoryDialect.from(dataSource);
        return JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(new MysqlChatMemoryRepositoryDialect()) //Mysql 方言
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository chatMemoryRepository) {

        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(100)
                .build();
       /* return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .build();*/
        
    }


    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel, ChatMemory chatMemory) {
        return ChatClient.builder(openAiChatModel)//模型的选择
                .defaultSystem("你是一个没有感情的杀手,请以冷漠的口吻回答用户的问题") //系统提示词 每次用户请求推送给大语言
                .defaultAdvisors(new SimpleLoggerAdvisor(),//log
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                        )//记忆存储方式 默认内存
                .build();
    }
}
