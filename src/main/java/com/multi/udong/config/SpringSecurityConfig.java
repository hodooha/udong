package com.multi.udong.config;

import com.multi.udong.login.service.AuthenticationService;
import com.multi.udong.login.service.CustomAuthenticationFailureHandler;
import com.multi.udong.login.service.CustomAuthenticationSuccessHandler;
import com.multi.udong.login.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * The type Spring security config.
 *
 * @author : 재식
 * @since : 24. 7. 21.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final DataSource dataSource;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationService authenticationService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    /**
     * B crypt password encoder b crypt password encoder.
     *
     * @return the b crypt password encoder
     * @since 2024 -08-02
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Web security customizer web security customizer.
     *
     * @return the web security customizer
     * @since 2024 -08-02
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/css/**", "/js/**", "/img/**", "/uploadFiles/**"
        );
    }

    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);

        return tokenRepository;
    }

    /**
     * Security filter chain security filter chain.
     *
     * @param http the http
     * @return the security filter chain
     * @throws Exception the exception
     * @since 2024 -08-02
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        Map<String, List<String>> permitListMap = authenticationService.getPermitListMap();
        List<String> adminPermitList = permitListMap.get("adminPermitList");
        List<String> permitAllList = permitListMap.get("permitAllList");

        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(adminPermitList.toArray(new String[0])).hasRole("ADMIN")
                .requestMatchers(permitAllList.toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                .loginPage("/login")
                .usernameParameter("memberId")
                .passwordParameter("memberPw")
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionFixation().migrateSession()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/")
            )
            .rememberMe(remember -> remember // https://bluexmas.tistory.com/1201
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(86400) // 24시간
                .userDetailsService(customUserDetailsService)
            )
            .userDetailsService(customUserDetailsService);
        return http.build();
    }

    /**
     * Authentication manager authentication manager.
     *
     * @param http the http
     * @return the authentication manager
     * @throws Exception the exception
     * @since 2024 -08-02
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }
}
