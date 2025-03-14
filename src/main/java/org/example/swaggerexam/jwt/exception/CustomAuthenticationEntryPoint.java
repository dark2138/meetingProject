package org.example.swaggerexam.jwt.exception;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // 시큐리티에서 인증되지 않은 사용자가 보호된 리소스에 접근 하려고 할때 동작하는 인터페이스
    // 사용자가 인증되지 않았을 때 어떻게 응답할지을 정의해주기 위해 사요
/*
AuthenticationEntryPoint는 마치 "출입 금지!" 팻말과 같아요.

만약 어떤 사람이 성🏰에 들어가려고 하는데, 왕자님인지 증명하지 못하면 (Authentication 실패),
 AuthenticationEntryPoint가 나타나서 "거기 멈춰! 당신은 들어갈 수 없습니다!"라고 외치는 거죠.

AuthenticationEntryPoint는 다음과 같은 역할을 합니다:

Authentication이 필요한데, 아직 인증되지 않은 사용자가 접근했을 때 작동: 성에 들어가려면 왕자님이라는 것을
증명해야 하는데, 아무것도 증명하지 못하고 그냥 막무가내로 들어가려고 할 때 나타나요.

어떻게 인증을 받아야 하는지 알려주는 역할: "신분증을 보여주세요!", "암호를 입력하세요!" 와 같이, 어떤 방법으로
인증을 받아야 하는지 알려주는 역할을 해요. 예를 들어, 로그인 페이지로 보내거나, "인증이 필요합니다"라는 메시지를 보여줄 수 있어요.

결론적으로, AuthenticationEntryPoint는 인증되지 않은 사용자의 접근을 막고, 올바른 인증 방법을 안내하는
중요한 역할을 합니다.

 */


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");

         if(isRestRequest(request)){
             // REST로 요청이 들어왔을 때 수행할 코드
             handleRestResponse(request, response, exception);
         }else{
             // PAGE로 요청이 들어왔을 때 수행할 코드
            // handlePageResponse(request, response, exception);

         }

    }

    // 페이지가 요청 중에 예외가 발생했다면, 로그남기고, 무조건 /loginform으로 리다이렉트
    private void handlePageResponse(HttpServletRequest request, HttpServletResponse response, String exception) throws IOException {
        log.error("Page Request - Commence Get Exception : {}", exception);

        if (exception != null) {
            // 추가적인 페이지 요청에 대한 예외 처리 로직을 여기에 추가할 수 있습니다.
        }

        response.sendRedirect("/loginform");
    }

    // ajax 통신
    // 이건 페이지야? 아니면 REST야?
    private boolean isRestRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With"); // ajax 만들때 나오는 객체
        return "XMLHttpRequest".equals(requestedWithHeader) || request.getRequestURI().startsWith("/api/");
        // "/api/" 는 내가 주는거
        // XMLHttpRequest 헤더 이름
    }

    private void setResponse(HttpServletResponse response, JwtExceptionCode exceptionCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 인증안된 사용자

        HashMap<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("message", exceptionCode.getMessage());
        errorInfo.put("code", exceptionCode.getCode());
        Gson gson = new Gson(); // json 형태로 만들어 줌
        String responseJson = gson.toJson(errorInfo);
        response.getWriter().print(responseJson);
    }

    private void handleRestResponse(HttpServletRequest request, HttpServletResponse response, String exception) throws IOException {
        log.error("Rest Request - Commence Get Exception : {}", exception);

        if (exception != null) {
            if (exception.equals(JwtExceptionCode.INVALID_TOKEN.getCode())) {
                log.error("entry point >> invalid token");
                setResponse(response, JwtExceptionCode.INVALID_TOKEN);
            } else if (exception.equals(JwtExceptionCode.EXPIRED_TOKEN.getCode())) {
                log.error("entry point >> expired token");
                setResponse(response, JwtExceptionCode.EXPIRED_TOKEN);
            } else if (exception.equals(JwtExceptionCode.UNSUPPORTED_TOKEN.getCode())) {
                log.error("entry point >> unsupported token");
                setResponse(response, JwtExceptionCode.UNSUPPORTED_TOKEN);
            } else if (exception.equals(JwtExceptionCode.NOT_FOUND_TOKEN.getCode())) {
                log.error("entry point >> not found token");
                setResponse(response, JwtExceptionCode.NOT_FOUND_TOKEN);
            } else {
                setResponse(response, JwtExceptionCode.UNKNOWN_ERROR);
            }
        } else {
            setResponse(response, JwtExceptionCode.UNKNOWN_ERROR);
        }
    }
}
