package com.chen.service;

import com.chen.pojo.dto.UserDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    void insertSingleUser(UserDTO user, HttpServletResponse response);
}
