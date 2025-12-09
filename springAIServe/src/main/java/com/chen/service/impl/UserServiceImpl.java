package com.chen.service.impl;

import ch.qos.logback.core.util.MD5Util;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.chen.constant.UserConstant;
import com.chen.exception.AccountRegisterException;
import com.chen.exception.LoginException;
import com.chen.mapper.UserMapper;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.User;
import com.chen.pojo.properties.JwtProperties;
import com.chen.service.UserService;
import com.chen.util.JwtUtil;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.chen.constant.RedisConstant.*;
import static com.chen.constant.ResultConstant.*;
import static com.chen.constant.ResultConstant.HTTPSTATUS.BAD_REQUEST;
import static com.chen.constant.ResultConstant.HTTPSTATUS.NOT_FOUND;
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

    @Autowired
    private JwtProperties jwtProperties;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String insertSingleUser(UserDTO userDTO, HttpServletResponse response) {

        if (userDTO == null || userDTO.getEmail() == null) {
            //zui弱智的校验，但是要加
            throw new AccountRegisterException(INPUT_PARAMS_ERR, BAD_REQUEST);
        }

        //邮箱验证码验证
        String key = USER_REGISTER + userDTO.getEmail();
        String originCode = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(originCode)) {
            throw new AccountRegisterException(UNCODE, BAD_REQUEST);
        }
        //  邮箱验证码的验证是否一致
        if (!originCode.equals(userDTO.getEmailCode())) {
            throw new AccountRegisterException(INPUT_CODE_ERR, BAD_REQUEST);
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
            throw new AccountRegisterException(PASSWORD_INVALID, BAD_REQUEST);
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
        //  token 返回

        //用户id回显
        Integer userId = user.getId();
        //邮箱
        String email = user.getEmail();
        Map<String, Object> claims = new HashMap<>();

        claims.put(USER_ID, userId);
        claims.put(EMAIL, email);
        String Key = jwtProperties.getUserSignKey();
        long userTTL = jwtProperties.getUserTtl();
        String token = JwtUtil.generateUserToken(Key, userTTL, claims);

        log.info("已经为id：{}的用户注册用户生成对应token", userId);

        return token;
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
            //反序列化 email就是去除双引号....
            String parseEmail = (String) JSONUtils.parse(email);
            String key = USER_REGISTER + parseEmail;
            Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, code, USER_REGISTER_TTL, TimeUnit.MILLISECONDS);
            if (BooleanUtil.isFalse(flag)) {
                //当前用户验证码未过期
                throw new AccountRegisterException(REPEAT_REQUEST_CODE, BAD_REQUEST);
            }
            //设置html处理变量
            Context context = new Context();
            context.setVariable("code", code);
            context.setVariable("nickName", nickName);
            //处理为字符串发送
            String template = templateEngine.process("emailTemplate", context);
            //确认发送文本
            mimeMessageHelper.setText(template, true);
            //收件人
            mimeMessageHelper.setTo(parseEmail);
            //发送
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("send email code error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateLoginCode(String email) {
        String code = RandomUtil.randomString(4);

        String key = USER_LOGIN + email;
        //TODO 加入防暴力破解的次数限制
        stringRedisTemplate.opsForValue().set(key, code);

        return code;
    }

    @Override
    public String querySingleUser(UserDTO user) {
        if (user == null || user.getEmail() == null) {
            throw new LoginException(INPUT_PARAMS_ERR);
        }

        String email = user.getEmail();
        User selected = userMapper.selectByEmail(email);
        //邮箱就不存在数据库
        if (selected == null) {
            throw new LoginException(USER_IEXIST_EMAIL, NOT_FOUND);
        }
        //用户输入密码加密校验
        String inputPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());

        //密码不正确
        if (!selected.getPassword().equals(inputPassword)) {
            throw new LoginException(USER_INPUT_PASS_ERR);
        }

        // 生成token 返回
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID, selected.getId());
        claims.put(EMAIL, email);
        String Key = jwtProperties.getUserSignKey();
        long userTTL = jwtProperties.getUserTtl();
        String token = JwtUtil.generateUserToken(Key, userTTL, claims);

        log.info("已经为id：{}的用户生成对应token", selected.getId());

        return token;
    }
}
