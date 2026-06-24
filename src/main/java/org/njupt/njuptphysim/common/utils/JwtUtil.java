package org.njupt.njuptphysim.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.njupt.njuptphysim.common.properties.JwtProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

// jwt工具类
@Component
public class JwtUtil {

    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt有效时间(毫秒)Time To Live Milliseconds
     * @param claims    设置的信息
     * @return 加密后的token令牌
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {

        // 生成JWT的时间
        Date now = new Date();
        // JWT过期时间
        Date exp = new Date(now.getTime()+ttlMillis);
/*
        // 指定签名的时候使用的签名算法，也就是header那部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // 设置jwt的body
        JwtBuilder builder1 = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                // 设置过期时间
                .setExpiration(exp);
*/
        // 生成JWT
        JwtBuilder builder = Jwts.builder()
                //.subject((String) claims.get("id"))
                .issuedAt(now)
                .expiration(exp)
                .claims(claims)
                .signWith(getSigningKey(secretKey), Jwts.SIG.HS256);

        return builder.compact();
    }

    /**
     * 生成 SecretKey 对象
     *
     * @param secretKey 密钥
     * @return secretKey
     */
    private static SecretKey getSigningKey(String secretKey) {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token     加密后的token
     * @return 解密后的信息claims
     */
    public static Claims parseJWT(String secretKey, String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey(secretKey))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token已过期: " + e.getMessage(), e);

        } catch (MalformedJwtException e) {
            throw new RuntimeException("Token格式错误: " + e.getMessage(), e);

        } catch (SecurityException e) {
            throw new RuntimeException("Token签名验证失败: " + e.getMessage(), e);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Token参数非法: " + e.getMessage(), e);

        } catch (JwtException e) {  // 所有 JWT 异常的父类
            throw new RuntimeException("Token验证失败: " + e.getMessage(), e);
        }
    }


    /**
     * 检查 Token 是否过期
     */
    public static Boolean isTokenExpired(Claims parseJWT) {
        Date expiration = parseJWT.getExpiration();
        return expiration.before(new Date());
    }


}
