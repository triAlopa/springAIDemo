package com.chen.controller;

import com.chen.pojo.Result;
import com.chen.pojo.dto.UserDTO;
import com.chen.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;

@RestController
@Slf4j
@RequestMapping("/user")
@Tag(name = "用户操作接口")

public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Result<UserDTO> register( @Validated(UserDTO.onRegister.class) @RequestBody UserDTO user,
                                     HttpServletResponse response) {

        log.info("用户请求注册：{}",user);

        userService.insertSingleUser(user,response);

        return Result.success();
    }



    @PostMapping("/emailCode/{nickName}")
    public Result<String> sendCode(@PathVariable String nickName,@RequestBody String email) {

        log.info("注册昵称为{} ,请求发送邮箱{} 验证码",nickName,email);

        userService.sendEmailCode(nickName,email);

        return Result.success("发送成功，查看你的邮箱");
    }

}
