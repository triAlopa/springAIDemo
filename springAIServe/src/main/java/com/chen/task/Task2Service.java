package com.chen.task;

import com.networknt.schema.format.DateTimeFormat;
import jakarta.validation.constraints.Null;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static com.chen.constant.RedisConstant.USER_LOGIN;

@Slf4j
@Component
public class Task2Service {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static DefaultRedisScript<Long> DEL_SCRIPT;

    static {
        DEL_SCRIPT = new DefaultRedisScript<>();
        DEL_SCRIPT.setResultType(Long.class);
        DEL_SCRIPT.setLocation(new ClassPathResource("lua/DeleteLoginCode.lua"));
    }

    /**
     * 处理删除前一天用户的垃圾验证码
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void delUserLoginCode() {
        String pattern = "yyyy-MM-dd";

        String date = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern(pattern));
        String key_prefix=date + ":*";
        String key = USER_LOGIN + key_prefix;

        Long execute = stringRedisTemplate.execute(
                DEL_SCRIPT,
                Collections.emptyList(),
                key
        );

        log.info("日期:{}一共删除了:{}条垃圾验证码", date, execute);
    }
}
