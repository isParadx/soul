package com.paradx.soul.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具类
 * 用于生成和解析Token
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt.token")
public class JwtHelper {
    private long tokenExpiration; //有效时间,单位分钟
    private String tokenSignKey;  //当前程序签名秘钥

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(tokenSignKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成token字符串
     * @param userId 用户ID
     * @return JWT Token
     */
    public String createToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenExpiration * 60 * 1000);
        
        return Jwts.builder()
                .subject("SOUL-USER")
                .expiration(expiration)
                .claim("userId", userId)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从token字符串获取userid
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserId(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            Object userIdObj = claims.get("userId");
            if (userIdObj instanceof Long) {
                return (Long) userIdObj;
            } else if (userIdObj instanceof Integer) {
                return ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Number) {
                return ((Number) userIdObj).longValue();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断token是否有效
     * @param token JWT Token
     * @return true-已过期或无效，false-有效
     */
    public boolean isExpiration(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证token是否有效
     * @param token JWT Token
     * @return true-有效，false-无效
     */
    public boolean validateToken(String token) {
        return !isExpiration(token);
    }
}
