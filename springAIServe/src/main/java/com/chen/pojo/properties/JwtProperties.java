package com.chen.pojo.properties;

import lombok.Data;
import org.apache.ibatis.annotations.Property;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chatai.jwt")
@Data
public class JwtProperties {

    /**
     * user token 设置
     */
    private String userSignKey;
    private long userTtl;
    private String userTokenName;
}
