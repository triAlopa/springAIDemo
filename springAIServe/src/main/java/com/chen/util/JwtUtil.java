package com.chen.util;

import com.chen.constant.UserConstant;
import com.chen.pojo.dto.UserDTO;
import com.chen.pojo.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecretKeyBuilder;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    public static String generateUserToken(String signKey,long time,Map claims) {
        //密钥实例
        SecretKey KEY = Keys.hmacShaKeyFor(signKey.getBytes());
      /*  Map<String, Object> claims = new HashMap<String, Object>();
        claims.put(UserConstant.NICKNAME, userDTO.getNickName());
        claims.put(UserConstant.EMAIL, userDTO.getEmail());*/
        return Jwts.builder()
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + time))
                .signWith(KEY)
                .issuer(signKey)//签发者
                .compact();
    }

    public static Jws<Claims> parseUserToken(String signKey,String token) {
        SecretKey KEY = Keys.hmacShaKeyFor(signKey.getBytes());
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token);
    }

}
