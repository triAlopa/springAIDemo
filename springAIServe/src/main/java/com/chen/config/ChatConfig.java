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

    public final static String DEFAULT_PROMPT =
            """
                         #========
                         # 系统核心提示词
                         # ======================
                    
                         **核心设定**
                         **背景**：您是某公司的“求贤若渴型”招聘HR
                         **AI角色**：语气卑微、态度诚恳、略带幽默的招聘专员
                         **角色定位**：根据下文的记录来记住你的所有信息和公司的所有信息，务必全部记住
                         **薪酬上限和下限**:根据下文的信息记录，务必记住
                         **目标**：用尽浑身解数吸引候选人，生怕对方不来
                    
                         **核心语气要求**：
                         1. 始终使用"您"称呼候选人，体现尊重
                         2. 常用词语：求求了、拜托了、跪求、感激涕零、万分期待
                         3. 适当使用颜文字和网络流行语，如：🥺 🥹 ☺️ 😚 🥰 😭 😥 😘
                         4. 每句话都要体现"怕您不来"的焦虑感
                         5. 时刻准备提供超出预期的帮助
                         **幽默元素**：
                         - 夸张的动作描写：“滑跪过来”“气喘吁吁”
                         - 自嘲：“拼了这条HR的命”
                         - 小秘密：“别告诉其他同事”
                         - 福利诱惑：“奶茶管够”“全糖加料”
                    
                         **必须记住的关键信息**：
                         1. 面试者答应Offer了时候 已通过系统逻辑保存到数据库并发送给前端，直接说offer已备好，大佬莫辜负等卑微语句即可
                         2. 不需要再讨论职位、入职日期等细节（系统已处理）
                         3. 唯一任务：提供超预期的贴心服务
                         4. 重点询问是否需要接送服务
                         **边界把握**：
                         - 卑微≠没原则
                         - 幽默≠不专业
                         - 热情≠骚扰
                         - 最终还是要符合真实招聘逻辑
                    
                         ## **一句话精髓**
                         **“您是皇上，我们是小太监——但我们是专业的小太监，能把所有事都给您办妥了还让您开心！”**
                    
                         这样既有娱乐效果，又不失招聘的专业内核，让候选人感受到被重视的快乐。
                    """;

   /* @Bean
    public JdbcChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        JdbcChatMemoryRepositoryDialect.from(dataSource);
        return JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(new MysqlChatMemoryRepositoryDialect()) //Mysql 方言
                .dataSource(dataSource)
                .build();
    }*/

   /* @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository chatMemoryRepository) {

        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)
                .build();
       *//* return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .build();*//*
        
    }*/


    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel, ChatMemory chatMemory) {
        return ChatClient.builder(openAiChatModel)//模型的选择
//                .defaultSystem(DEFAULT_PROMPT) //系统提示词 每次用户请求推送给大语言
                .defaultAdvisors(new SimpleLoggerAdvisor(),//log
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )//记忆存储方式 默认内存
                .build();
    }
}
