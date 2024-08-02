package com.multi.udong.login.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * The type Custom authentication success handler.
 *
 * @author 김재식
 * @since 2024 -08-02
 */
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final LoginService loginService;

    /**
     * Instantiates a new Custom authentication success handler.
     *
     * @param loginService the login service
     */
    public CustomAuthenticationSuccessHandler(LoginService loginService) {
        this.loginService = loginService;
        setDefaultTargetUrl("/");
        setAlwaysUseDefaultTargetUrl(true);
    }

    /**
     * On authentication success.
     *
     * @param request        the request
     * @param response       the response
     * @param authentication the authentication
     * @throws ServletException the servlet exception
     * @throws IOException      the io exception
     * @since 2024 -08-02
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        String memberId = authentication.getName();
        loginService.updateLastLoginAt(memberId);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}