package com.multi.udong.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

@Component
public class CsrfTokenProvider {
    @Autowired
    private HttpServletRequest request;

    public String getToken() {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return token != null ? token.getToken() : null;
    }
}