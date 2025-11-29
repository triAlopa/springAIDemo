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
public class UserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    @NotBlank(groups = onRegister.class,message = nickNameUNVALID)
    private String nickName;
    @NotNull(groups = onRegister.class,message = genderUNVALID)
    private Integer gender;
    @NotNull(groups = onRegister.class,message = birthdayNVALID)
    private Date birthday;
    @NotBlank(groups = {onRegister.class,onLogin.class},message = passwordUNVALID)
    private String password;
    @NotBlank(groups = {onRegister.class,onLogin.class},message = emailUNVALID)
    @Email(groups = {onRegister.class,onLogin.class},regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = emailUNVALIDWITHREGX)
    private String email;

    public interface onRegister{}
    public interface onLogin{}
}
