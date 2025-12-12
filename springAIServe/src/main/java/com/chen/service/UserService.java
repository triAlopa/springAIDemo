package com.chen.service;

import com.chen.pojo.dto.UserChangePassDTO;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.vo.AISessionVo;
import com.chen.pojo.vo.UserVo;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface UserService {

    String insertSingleUser(UserDTO user, HttpServletResponse response);

    void sendEmailCode(String nickName, String email);

    String generateLoginCode(String email);

    String querySingleUser(UserDTO user);

    UserVo queryUserInfo();

    String modifyUserPassword(Integer userId, UserChangePassDTO changePassDTO);
}
