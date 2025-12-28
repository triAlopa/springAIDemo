package com.chen.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@Slf4j
public class RedisConfiguration {



    @Value("${spring.data.redis.host:redis}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;


    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("创建Redis连接工厂，参数: host={}, port={}, database={}",
                redisHost, redisPort, redisDatabase);

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setPassword("06170018");
        config.setDatabase(redisDatabase);

        log.info("Redis连接配置 - Host: {}, Port: {}, Database: {} Password: {}",
                config.getHostName(), config.getPort(), config.getDatabase(),config.getPassword());

        // 3. 创建连接工厂
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);

        // 4. 初始化
        factory.afterPropertiesSet();

        log.info("Redis连接工厂创建完成: {}", factory);
        return factory;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("创建StringRedisTemplate，使用连接工厂: {}", redisConnectionFactory);

        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);

        // 初始化模板
        template.afterPropertiesSet();

        log.info("StringRedisTemplate创建完成");
        return template;
    }
}