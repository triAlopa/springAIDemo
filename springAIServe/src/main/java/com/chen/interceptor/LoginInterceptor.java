package com.chen.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.chen.constant.UserConstant;
import com.chen.exception.LoginException;
import com.chen.mapper.UserMapper;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.User;
import com.chen.pojo.properties.JwtProperties;
import com.chen.util.CurrentUserHolder;
import com.chen.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.annotation.Resource;
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

import static com.chen.constant.ResultConstant.HTTPSTATUS.UNAUTHORIZED;
import static com.chen.constant.ResultConstant.HTTPSTATUS.UNKNOWN_ERROR;
import static com.chen.constant.ResultConstant.UNAUTHMSG;
import static com.chen.constant.UserConstant.EMAIL;

@Slf4j
@Component
@NoArgsConstructor
@AllArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//        String userTokenName = jwtProperties.getUserTokenName();
        /*String token = request.getHeader(userTokenName);*/
        String userTokenName = jwtProperties.getUserTokenName();

        String userTokenId = request.getHeader(userTokenName);

//        String json = stringRedisTemplate.opsForValue().get(userTokenId);
        String token = stringRedisTemplate.opsForValue().get(userTokenId);

        // 对无token的用户处理不够的妥当 cause by SSE
        String uri = request.getRequestURI();
        if ("/user/ai/send".equals(uri)) return true;

        //没有token
        if (StrUtil.isBlank(token) || "null".equals(token)) {
            throw new LoginException(UNAUTHMSG, UNAUTHORIZED);
        }
        Jws<Claims> claims = null;
        try {
            claims = JwtUtil.parseUserToken(jwtProperties.getUserSignKey(), token);
        } catch (Exception e) {
            //token解析错误，伪装或者失效
            log.warn("用户token：{}解析错误", token);
            throw new LoginException(UNAUTHMSG, UNAUTHORIZED);
        }
        String email = claims.getPayload().get(EMAIL, String.class);

        User user = userMapper.selectByEmail(email);

        //用户不存在，拦截请求
        if (user == null) {
            throw new LoginException(UNAUTHMSG, UNAUTHORIZED);
        }

        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(user, userDTO, true);
        //该线程存储使用该用户//结束移除
        CurrentUserHolder.setCurrentUser(userDTO);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("init login handler");
        CurrentUserHolder.removeCurrentUser();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("destroy login handler");
    }
}
