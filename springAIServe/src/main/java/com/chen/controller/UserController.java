package com.chen.controller;

import com.chen.pojo.Result;
import com.chen.pojo.dto.UserDTO;
import com.chen.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/user")
@Tag(name = "用户操作接口")

public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Result<UserDTO> register(@Validated(UserDTO.onRegister.class) @RequestBody UserDTO user,
                                    HttpServletResponse response) {

        log.info("用户请求注册：{}", user);

        userService.insertSingleUser(user, response);

        return Result.success();
    }

    @PostMapping("/login")
    public Result<UserDTO> login(@Validated(UserDTO.onLogin.class) @RequestBody UserDTO user) {

        log.info("用户请求登录：{}", user);

        userService.querySingleUser(user);

        return Result.success();
    }


    @PostMapping("/emailCode/{nickName}")
    public Result<String> sendEmailCode(@PathVariable String nickName, @RequestBody String email) {

        log.info("注册昵称为{} ,请求发送邮箱{} 验证码", nickName, email);

        userService.sendEmailCode(nickName, email);

        return Result.success("发送成功，查看你的邮箱");
    }

    @GetMapping("/login/{email}")
    public Result<String> sendLoginCode(@PathVariable(name = "email") String email) {

        log.info("登录邮箱为{} ,请求生成验证码", email);

        String code = userService.generateLoginCode(email);

        return Result.success(code);
    }

}
