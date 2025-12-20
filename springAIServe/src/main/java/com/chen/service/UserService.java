package com.chen.service;

import com.chen.pojo.PageResult;
import com.chen.pojo.dto.QueryUserDTO;
import com.chen.pojo.dto.UserChangePassDTO;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.vo.UserVO;
import com.chen.pojo.vo.report.EmailReportVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {

    String insertSingleUser(UserDTO user, HttpServletResponse response);

    void sendEmailCode(String nickName, String email);

    String generateLoginCode(String email);

    String querySingleUser(UserDTO user);

    UserVO queryUserInfo();

    String modifyUserPassword(Integer userId, UserChangePassDTO changePassDTO);

    String uploadUserImage(MultipartFile file);

    PageResult<List<UserVO>>  queryAllUser(QueryUserDTO queryUserDTO);

    void updateSingleUser(UserDTO user);

    UserVO selectById(Integer userId);

    void deleteUserByLogical(Integer id);

    void deleteUsersByLogical(List<Integer> ids);

    List queryUserRegisterInfo(LocalDateTime time);

    List<EmailReportVO> getEmailStats();

    void getUserReportExcel(HttpServletResponse response);

}
