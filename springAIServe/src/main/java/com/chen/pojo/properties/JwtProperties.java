package com.chen.pojo.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chatai.jwt")
@Data
@NoArgsConstructor
public class JwtProperties {

    /**
     * user token 设置
     */
    private String userSignKey;
    private long userTtl;
    private String userTokenName;
}
