package com.chen;

import com.chen.constant.UserConstant;
import com.chen.mapper.AIMessageMapper;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.entity.AIMessage;
import com.chen.pojo.properties.JwtProperties;
import com.chen.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ComponentScan("com.chen")
class SpringAiDemoApplicationTests {


    @Autowired
    private AIMessageMapper aiMessageMapper;
    @Test
    void contextLoads() {
        List<AIMessage> messages = aiMessageMapper.getMessages("1");
        System.out.println(messages);
    }

   @Autowired
   private JwtProperties jwtProperties;


    /**
     * 测试用户的jwt生成解析
     */
    @Test
    void testJwtUtil(){
         String signKey = jwtProperties.getUserSignKey();
         long time = jwtProperties.getUserTtl();
        UserDTO build = UserDTO.builder()
                .nickName("chen")
                .email("chen@gmail.com")
                .password("123456")
                .build();

        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put(UserConstant.NICKNAME, build.getNickName());
        claims.put(UserConstant.EMAIL, build.getEmail());

        String token = JwtUtil.generateUserToken(signKey,time,claims);
        System.out.println(token);
        Jws<Claims> claimsJws = JwtUtil.parseUserToken(signKey,token);
        String nickName = claimsJws.getPayload().get("nickName", String.class);
        System.out.println(nickName);


       /* Map<String, Object> claims = new HashMap<String, Object>();
        claims.put(UserConstant.NICKNAME, build.getNickName());
        claims.put(UserConstant.EMAIL, build.getEmail());

        SecretKey key = Jwts.SIG.HS256.key().build();
        String token = Jwts.builder()
                .signWith(key)
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .compact();
        System.out.println(token);

        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

        System.out.println(claimsJws.getPayload().get("nickName", String.class));*/

    }

    @Test
    void testSubStr(){
        String str="org.springframework.dao.DuplicateKeyException: \n" +
                "### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '1@qq.com' for key 'tb_ai_user.email'\n" +
                "### The error may exist in com/chen/mapper/UserMapper.java (best guess)\n" +
                "### The error may involve com.chen.mapper.UserMapper.insert-Inline\n" +
                "### The error occurred while setting parameters\n" +
                "### SQL: insert into tb_ai_user(nick_name, gender, birthday, password, email, points, enable, is_del, register_time) value (?,?,?,?,?,?,?,?,?)\n" +
                "### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '1@qq.com' for key 'tb_ai_user.email'\n" +
                "; Duplicate entry '1@qq.com' for key 'tb_ai_user.email";
        int index = str.indexOf(" for key ");
        String duplicateEntry = str.substring(index-10, index);
        System.out.println(duplicateEntry);
    }


  @Test
    void testMatch(){
        String regex="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(regex);

        pattern.matcher("chen@gmail.com").matches();
        System.out.println(pattern.matcher("chen@gmail.com").matches());

      System.out.println(pattern.matcher("1111").matches());
  }

  @Test
    void testJwtProperties(){
      System.out.println(jwtProperties);
  }

}
