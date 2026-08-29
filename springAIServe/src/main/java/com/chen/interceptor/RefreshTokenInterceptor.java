package com.chen.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.chen.exception.LoginException;
import com.chen.mapper.UserMapper;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.User;
import com.chen.pojo.properties.JwtProperties;
import com.chen.service.UserService;
import com.chen.util.CurrentUserHolder;
import com.chen.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.chen.constant.RedisConstant.USER_TOKEN;
import static com.chen.constant.RedisConstant.USER_TOKEN_TTL;
import static com.chen.constant.ResultConstant.HTTPSTATUS.UNAUTHORIZED;
import static com.chen.constant.ResultConstant.UNAUTHMSG;
import static com.chen.constant.UserConstant.EMAIL;
import static com.chen.constant.UserConstant.USER_ID;

@Slf4j
@Component
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenInterceptor implements HandlerInterceptor {

    /*@Resource
    private JwtUtil jwtUtil;*/

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        if ("/user/ai/send".equals(uri)) return true;

        String userTokenName = jwtProperties.getUserTokenName();

        String userTokenId = request.getHeader(userTokenName);

        if (StrUtil.isBlank(userTokenId) || "null".equals(userTokenId)) {
            return true;
        }

        String token = stringRedisTemplate.opsForValue().get(userTokenId);
   /*     String token = objectMapper.readValue(json, String.class);*/

/*        String token = request.getHeader(userTokenName);*/

        // 对无token的用户处理不够的妥当 cause by SSE
        //没有token
        if (StrUtil.isBlank(token) || "null".equals(token)) {
            return true;
        }
        Jws<Claims> claims = null;
        try {
            claims = JwtUtil.parseUserToken(jwtProperties.getUserSignKey(), token);
        } catch (Exception e) {
            //token解析错误，伪装或者失效
            log.warn("用户token：{}解析错误", token);
            return true;
        }
        String email = claims.getPayload().get(EMAIL, String.class);

        User user = userMapper.selectByEmail(email);

        //用户不存在，拦截请求
        if (user == null) {
            return true;
        }


        log.info("已经为id：{}的用户刷新对应token", user.getId());

        String userTokenValue = userService.generateUserToken(user);

        String userTokenKey =USER_TOKEN+user.getId();

        stringRedisTemplate.opsForValue().set(
                userTokenKey,
                userTokenValue,
                USER_TOKEN_TTL,
                TimeUnit.MILLISECONDS
        );

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("init refresh handler");
        CurrentUserHolder.removeCurrentUser();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("destroy refresh handler");
    }
}
