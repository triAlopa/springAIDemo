package com.chen.service.impl;

import ch.qos.logback.core.util.MD5Util;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.chen.constant.UserConstant;
import com.chen.exception.AccountRegisterException;
import com.chen.exception.LoginException;
import com.chen.mapper.UserMapper;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.User;
import com.chen.service.UserService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.chen.constant.RedisConstant.USER_REGISTER;
import static com.chen.constant.RedisConstant.USER_REGISTER_TTL;
import static com.chen.constant.UserConstant.*;
import static com.chen.constant.UserConstant.RegisterOrLOGIN.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private TemplateEngine templateEngine;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSingleUser(UserDTO userDTO, HttpServletResponse response) {

        if (userDTO == null || userDTO.getEmail() == null) {
            //zui弱智的校验，但是要加
            throw new AccountRegisterException("非法操作");
        }

        //邮箱验证码验证
        String key=USER_REGISTER+userDTO.getEmail();
        String originCode = stringRedisTemplate.opsForValue().get(key);
        if(StrUtil.isBlank(originCode)) {
            throw new AccountRegisterException(UNCODE);
        }
        //TODO 邮箱验证码的验证是否一致
        if(originCode.equals(userDTO)) {}

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

    @Override
    public void sendEmailCode(String nickName, String email) {
        //复杂的消息处理
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            //发送者
            mimeMessageHelper.setFrom(from);
            mimeMessage.setSubject("boss求聘平台验证码");
            //为邮箱html变量赋值
            //1.生成随机验证码
            String code = RandomUtil.randomString(6);
            //2.保留时间 超时失效
            String key=USER_REGISTER+email;
            stringRedisTemplate.opsForValue().set(key,code,USER_REGISTER_TTL, TimeUnit.MILLISECONDS);
            //设置html处理变量
            Context context = new Context();
            context.setVariable("code", code);
            context.setVariable("nickName", nickName);
            //处理为字符串发送
            String template = templateEngine.process("emailTemplate", context);
            //确认发送文本
            mimeMessageHelper.setText(template, true);
            //收件人
            String parseEmail = (String) JSONUtils.parse(email);
            mimeMessageHelper.setTo(parseEmail);
            //发送
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
