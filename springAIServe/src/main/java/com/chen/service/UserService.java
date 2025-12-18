package com.chen.service;

import com.chen.pojo.dto.UserChangePassDTO;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    String insertSingleUser(UserDTO user, HttpServletResponse response);

    void sendEmailCode(String nickName, String email);

    String generateLoginCode(String email);

    String querySingleUser(UserDTO user);

    UserVO queryUserInfo();

    String modifyUserPassword(Integer userId, UserChangePassDTO changePassDTO);

    String uploadUserImage(MultipartFile file);
}
