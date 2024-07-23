package com.multi.udong.config;

import com.multi.udong.security.AuthenticationService;
import com.multi.udong.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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

    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationService authenticationService;

    /**
     * B crypt password encoder b crypt password encoder.
     *
     * @return the b crypt password encoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Web security customizer web security customizer.
     *
     * @return the web security customizer
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/css/**", "/js/**", "/img/**", "/uploadFiles/**"
        );
    }

    /**
     * Security filter chain security filter chain.
     *
     * @param http the http
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        Map<String, List<String>> permitListMap = authenticationService.getPermitListMap();
        List<String> adminPermitList = permitListMap.get("adminPermitList");
        List<String> memberPermitList = permitListMap.get("memberPermitList");

        http
                .authorizeHttpRequests((requests) -> requests
//                        .requestMatchers("/").permitAll()
//                        .requestMatchers("/login").permitAll()
//                        .requestMatchers("/signup").permitAll()
//                        .requestMatchers(memberPermitList.toArray(new String[0])).hasAnyRole("MEMBER", "ADMIN")
//                        .requestMatchers(adminPermitList.toArray(new String[0])).hasRole("ADMIN")
//                        .anyRequest().authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .usernameParameter("memberId")
                        .passwordParameter("memberPw")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
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
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .sessionManagement(session -> session
                        .invalidSessionUrl("/login")
                )
                .userDetailsService(customUserDetailsService);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }
}
