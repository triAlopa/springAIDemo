package com.chen.service.impl;

import ch.qos.logback.core.util.MD5Util;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.chen.constant.UserConstant;
import com.chen.exception.AccountRegisterException;
import com.chen.exception.LoginException;
import com.chen.mapper.UserMapper;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.User;
import com.chen.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

import static com.chen.constant.UserConstant.*;
import static com.chen.constant.UserConstant.RegisterOrLOGIN.UNREGISTER;
import static com.chen.constant.UserConstant.RegisterOrLOGIN.passwordUNVALID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSingleUser(UserDTO userDTO, HttpServletResponse response) {

        if (userDTO == null || userDTO.getEmail() == null) {
            //zui弱智的校验，但是要加
            throw new AccountRegisterException("非法操作");
        }
        //校验用户是否注册
        User checkUser = userMapper.selectByEmail(userDTO.getEmail());
        if (checkUser != null) {
            //表示已经注册过了
            //已经在全局异常处理了,鸡蛋几个日志
            log.error(UNREGISTER);
        }
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(password)) {
            //密码为空
            throw new AccountRegisterException(passwordUNVALID);
        }
        //密文存储
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        userDTO.setPassword(password);

        //转为实体类 插入数据库
        User user = User.
                builder()
                .registerTime(LocalDateTime.now())
                .enable(ENABLE)
                .isDel(NODEL)
                .points(DEFAULT_POINTS)
                .build();
        BeanUtil.copyProperties(userDTO, user);

        userMapper.insert(user);
    }
}
