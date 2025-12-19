package com.chen.pojo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import static com.chen.constant.UserConstant.RegisterOrLOGIN.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * 前端交互使用
 */
public class UserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    @NotBlank(groups = {onRegister.class,onAdmin.class},message = NICKNAME_INVALID)
    private String nickName;
    @NotNull(groups ={onRegister.class,onAdmin.class},message = GENDER_INVALID)
    private Integer gender;
    @NotNull(groups = {onRegister.class,onAdmin.class},message = BIRTHDAY_INVALID)
    private Date birthday;
    @NotBlank(groups = {onRegister.class,onLogin.class },message = PASSWORD_INVALID)
    private String password;
    @NotBlank(groups = {onRegister.class,onLogin.class,onAdmin.class},message = EMAIL_INVALID)
    @Email(groups = {onRegister.class,onLogin.class,onAdmin.class},regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = EMAIL_INVALID_WITH_REGX)
    private String email;
    @NotBlank(groups = {onRegister.class},message = EMAIL_CODE_INVALID)
    private String emailCode;

    @NotBlank(groups = {onLogin.class},message = EMAIL_CODE_INVALID)
    private String loginCode;

    private String image;
    private Integer points;

    public interface onRegister{}
    public interface onLogin{}
    public interface onAdmin{}
}
