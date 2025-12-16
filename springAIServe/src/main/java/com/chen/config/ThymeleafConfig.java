package com.chen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class ThymeleafConfig {

    @Bean
    public ITemplateResolver iTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver ();
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(1);
        resolver.setCacheable(false);
        resolver.setPrefix("templates/text/");
        resolver.setTemplateMode(TemplateMode.TEXT);
        resolver.setSuffix(".txt");
        return resolver;
    }

    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(iTemplateResolver());
        return engine;
    }
}
