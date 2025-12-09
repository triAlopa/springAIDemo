package com.chen.service;

import com.chen.pojo.dto.UserDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    String insertSingleUser(UserDTO user, HttpServletResponse response);

    void sendEmailCode(String nickName, String email);

    String generateLoginCode(String email);

    String querySingleUser(UserDTO user);
}
