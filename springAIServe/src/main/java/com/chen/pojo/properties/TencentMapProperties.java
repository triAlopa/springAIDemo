package com.chen.pojo.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tencent.map")
@Data
@NoArgsConstructor
public class TencentMapProperties {

    /**
     *  腾讯地图 api配置
     */
    private String apiKey;
    private String secretKey;
}
