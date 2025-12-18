package com.chen.controller;

import cn.hutool.core.lang.UUID;
import com.chen.pojo.Result;
import com.chen.pojo.dto.UserChangePassDTO;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.vo.UserVO;
import com.chen.service.UserService;
import com.chen.util.AliyunOSSOperator;
import com.chen.util.CurrentUserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/user")
@Tag(name = "用户相关API", description = "用户注册登录对应接口")
public class UserController {

    @Resource
    private UserService userService;




    /**
     * 用户申请注册
     *
     * @param user
     * @param response
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "用户申请注册", description = "用户申请注册")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "用户填入表单&业务逻辑错误")
    })
    public Result<String> register(@Validated(UserDTO.onRegister.class) @RequestBody @Schema UserDTO user,
                                   HttpServletResponse response) {

        log.info("用户请求注册：{}", user);

        String token = userService.insertSingleUser(user, response);

        return Result.success(token);
    }

    /**
     * 用户登录
     *
     * @param user
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户填入信息获取数据库进行校验")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "用户填入表单&业务逻辑错误"),
            @ApiResponse(responseCode = "404", description = "找不到用户")
    })
    public Result<String> login(@Validated(UserDTO.onLogin.class) @RequestBody UserDTO user) {

        log.info("用户请求登录：{}", user);

        String token = userService.querySingleUser(user);

        return Result.success(token);
    }

    /**
     * 用户注册邮箱验证
     *
     * @param nickName
     * @param email
     * @return
     */
    @PostMapping("/emailCode/{nickName}")
    @Operation(summary = "用户注册邮箱验证", description = "请求发送邮箱验证")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "邮件发送者配置错误")
    })
    public Result<String> sendEmailCode(@Schema @PathVariable @NotNull String nickName, @RequestParam("email") @Schema @Email(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "邮箱格式错误!"
    ) String email) {

        log.info("注册昵称为{} ,请求发送邮箱{} 验证码", nickName, email);

        userService.sendEmailCode(nickName, email);

        return Result.success("发送成功，查看你的邮箱");
    }

    @GetMapping("/login/{email}")
    @Operation(summary = "用户登录验证码", description = "请求后端绑定对应的验证码")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "redis繁忙 业务逻辑")
    })
    public Result<String> sendLoginCode(@PathVariable(name = "email") @Email(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "邮箱格式错误!"
    ) String email) {

        log.info("登录邮箱为{} ,请求生成验证码", email);

        String code = userService.generateLoginCode(email);

        return Result.success(code);
    }

    @Operation(summary = "获取用户信息", description = "根据前端请求头携带token,获取用户的信息,返回前端展示")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "获取失败")
    })
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {

        log.info("获取本人的用户信息");

        UserVO userVo = userService.queryUserInfo();

        return Result.success(userVo);
    }

    @Operation(summary = "修改用户密码", description = "根据前端请求头携带token,获取用户的信息,修改用户密码")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "401", description = "原密码错误")
    })
    @PostMapping("/modifyPass")
    public Result responseChangePassword(@RequestBody UserChangePassDTO changePassDTO) {

        Integer userId = CurrentUserHolder.getCurrentUser().getId();
        log.info("用户：{}，尝试修改用户密码：{}", userId, changePassDTO);

        String token = userService.modifyUserPassword(userId, changePassDTO);

        return Result.success(token);
    }

    @Operation(summary = "修改用户密码", description = "根据前端请求头携带token,获取用户的信息,修改用户密码")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "401", description = "原密码错误")
    })
    @PostMapping("/upload")
    public Result<String> uploadUserImage(MultipartFile file) {

        Integer userId = CurrentUserHolder.getCurrentUser().getId();
        log.info("用户：{}，尝试上传文件：{}", userId, file);

        String url = userService.uploadUserImage(file);

        return Result.success(url);
    }

}
