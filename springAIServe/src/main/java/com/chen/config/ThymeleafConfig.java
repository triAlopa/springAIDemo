package com.chen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class ThymeleafConfig {


    /**
     * HTML
     * @return
     */
    @Primary
    @Bean
    public ITemplateResolver iTemplateResolver1() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver ();
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(1);
        resolver.setCacheable(false);
        resolver.setPrefix("templates/");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setSuffix(".html");
        return resolver;
    }

    /**
     * TXT
     * @return
     */
    @Bean
    public ITemplateResolver iTemplateResolver2() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver ();
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(2);
        resolver.setCacheable(false);
        resolver.setPrefix("templates/text/");
        resolver.setTemplateMode(TemplateMode.TEXT);
        resolver.setSuffix(".txt");
        return resolver;
    }

    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        Set<ITemplateResolver> resolvers=new HashSet<>();
        //先添加1
        resolvers.add(iTemplateResolver1());
        //在添加2
        resolvers.add(iTemplateResolver2());
        engine.setTemplateResolvers(resolvers);
        return engine;
    }
}
