package com.chen.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .description("这是文档的描述")
                        .contact(new Contact().name("chen")
                                .email("example@qq.com")
                                .url("www.example.com")
                        ).title("文档标题")
                        .version("1.0.0")
                        .license(new License()
                                .name("apache")
                                .url("http://springdoc.org")));
    }
}