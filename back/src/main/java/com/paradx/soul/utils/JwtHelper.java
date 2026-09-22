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
     * 生成token字符串（包含角色信息）
     * @param userId 用户ID
     * @param role 用户角色（0-学生, 1-医生, 2-管理员）
     * @return JWT Token
     */
    public String createToken(Long userId, Integer role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenExpiration * 60 * 1000);
        
        return Jwts.builder()
                .subject("SOUL-USER")
                .expiration(expiration)
                .claim("userId", userId)
                .claim("role", role)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成token字符串（兼容旧调用）
     * @param userId 用户ID
     * @return JWT Token
     */
    public String createToken(Long userId) {
        return createToken(userId, null);
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
            Claims claims = parseClaims(token);
            
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
     * 从token字符串获取用户角色
     * @param token JWT Token
     * @return 用户角色（0-学生, 1-医生, 2-管理员），无法获取时返回null
     */
    public Integer getUserRole(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            Claims claims = parseClaims(token);
            Object roleObj = claims.get("role");
            if (roleObj instanceof Integer) {
                return (Integer) roleObj;
            } else if (roleObj instanceof Number) {
                return ((Number) roleObj).intValue();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析Token获取Claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
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
