package org.example.swaggerexam.jwt.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.swaggerexam.jwt.exception.JwtExceptionCode;
import org.example.swaggerexam.jwt.service.CustomUserDetailsService;
import org.example.swaggerexam.jwt.token.JwtAuthenticationToken;
import org.example.swaggerexam.jwt.utill.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = getToken(request);


        if(StringUtils.hasText(token)) {

            try{

                Authentication authentication = getAuthentication(token);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (TokenExpiredException e) {
                request.setAttribute("exception",  JwtExceptionCode.EXPIRED_TOKEN);
                log.error("만료된 토큰 : {}", token, e);

                SecurityContextHolder.clearContext();

            } catch (JWTVerificationException e) {
                request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN);
                log.error("잘못된 서명이나 클레임이 포함된 토큰 : {}", token, e);

                SecurityContextHolder.clearContext();


            } catch (IllegalArgumentException e) {
                request.setAttribute("exception", JwtExceptionCode.NOT_FOUND_TOKEN.getCode());
                log.error("Authorization 헤더가 없거나 Bearer 접두사 누락 : {}", token, e);

                SecurityContextHolder.clearContext();

                throw new BadCredentialsException("Token not found exsception", e);
            } catch (Exception e) {
                log.error("JWT Fiter 1 - Internal Error : {}", token, e);

                SecurityContextHolder.clearContext();

                throw new BadCredentialsException("JWT Fiter 2 - Internal Error");
            }

        }

        filterChain.doFilter(request, response);

    }

    private Authentication getAuthentication(String token) {
        String email =  jwtUtil.validateAccessToken(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        return new JwtAuthenticationToken(userDetails);

    }

    private String getToken(HttpServletRequest request) {
        //헤더를 통해서 토큰을 넘겨줬다면...
        String authorization = request.getHeader("Authorization");

        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        //쿠키에 accessToken 이 있는지 찾아서 리턴
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
