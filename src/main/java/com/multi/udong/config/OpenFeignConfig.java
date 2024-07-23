package com.multi.udong.config;

import com.multi.udong.security.CsrfTokenProvider;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableFeignClients("com.multi.udong")
public class OpenFeignConfig {

    private final CsrfTokenProvider csrfTokenProvider;

    @Bean
    public RequestInterceptor csrfRequestInterceptor() {
        return requestTemplate -> {
            // CSRF 토큰을 쿠키에서 가져옵니다.
            String csrfToken = csrfTokenProvider.getToken();
            if (csrfToken != null) {
                requestTemplate.header("X-CSRF-TOKEN", csrfToken);
            }
        };
    }

}
