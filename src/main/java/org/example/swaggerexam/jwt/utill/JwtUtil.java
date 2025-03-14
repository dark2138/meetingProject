package org.example.swaggerexam.jwt.utill;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {

    // ACCESS 및 REFRESH 토큰 생성에 사용할 비밀 키를 저장하는 필드
    private final byte[] accessSecret; // ACCESS 토큰 서명에 사용할 비밀 키
    private final byte[] refreshSecret; // REFRESH 토큰 서명에 사용할 비밀 키


    public static final Long ACCESS_TOKEN_EXPIRES_COUNT = 30 * 60 * 1000L; // ACCESS 토큰 만료 시간: 30분 (밀리초 단위)
    public static final Long REFRESH_TOKEN_EXPIRES_COUNT = 7 * 24 * 60 * 60 * 1000L; // REFRESH 토큰 만료 시간: 7일 (밀리초 단위)

    public JwtUtil(@Value("${jwt.secretKey}") String accessSecret,
                   @Value("${jwt.refreshKey}") String refreshSecret) {
        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8); // ACCESS 비밀 키를 UTF-8 바이트 배열로 변환
        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8); // REFRESH 비밀 키를 UTF-8 바이트 배열로 변환
    }


    private final ConcurrentHashMap<String, Boolean> invalidTokens = new ConcurrentHashMap<>();

    // ACCESS 토큰 생성
    public String generateAccessToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRES_COUNT))
                .sign(Algorithm.HMAC256(accessSecret));
    }

    // REFRESH 토큰 생성
    public String generateRefreshToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRES_COUNT))
                .sign(Algorithm.HMAC256(refreshSecret));
    }

    // ACCESS 토큰 검증
    public String validateAccessToken(String token) {
        try {
            if (invalidTokens.containsKey(token)) {
                return null;
            }
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(accessSecret)).build().verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException | NumberFormatException e) {
            return null;
        }
    }

    // REFRESH 토큰 검증
    public String validateRefreshToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(refreshSecret)).build().verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }


    public void invalidateToken(String token) {

        invalidTokens.put(token, true);

    }
}
