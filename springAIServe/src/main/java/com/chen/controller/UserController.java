package com.chen.controller;

import com.chen.pojo.Result;
import com.chen.pojo.dto.UserDTO;
import com.chen.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
