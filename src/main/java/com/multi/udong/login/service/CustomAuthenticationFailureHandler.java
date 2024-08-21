package com.multi.udong.login.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorMessage;
        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디 또는 비밀번호가 올바르지 않습니다.";
        } else if (exception instanceof LockedException) {
            errorMessage = "신고가 누적되어<br>블랙리스트에 등록되었습니다..";
        } else {
            errorMessage = "로그인에 실패했습니다.";
        }

        setDefaultFailureUrl("/login?error=true&message=" + URLEncoder.encode(errorMessage, "UTF-8"));
        super.onAuthenticationFailure(request, response, exception);
    }
}