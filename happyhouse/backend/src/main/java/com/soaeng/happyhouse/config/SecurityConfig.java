package com.soaeng.happyhouse.config;

import com.soaeng.happyhouse.filter.LoginFilter;
import com.soaeng.happyhouse.handler.RefreshTokenLogoutHandler;
import com.soaeng.happyhouse.jwt.service.JwtService;
import com.soaeng.happyhouse.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationSuccessHandler loginSuccessHandler;
    private final AuthenticationSuccessHandler socialSuccessHandler;
    private final JwtService jwtService;
    private final JwtUtil jwtUtil;


    // 비밀번호 단방향 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // CSRF 보안 필터 disable
                .csrf(AbstractHttpConfigurer::disable)
                // HTTP Basic 인증 방식 disable
                .httpBasic(AbstractHttpConfigurer::disable)
                // Form 로그인 방식 disable
                .formLogin(AbstractHttpConfigurer::disable)
                // oauth2
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(socialSuccessHandler))
                // Custom Filter 추가
                .addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration), loginSuccessHandler), UsernamePasswordAuthenticationFilter.class)
                // 경로별 인가 작업
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                // 예외 처리
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 401 응답
                        })
                        .accessDeniedHandler((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403 응답
                        })
                )
                // logout
                .logout(logout -> logout
                        .addLogoutHandler(new RefreshTokenLogoutHandler(jwtUtil, jwtService)))
                // 세션 설정: STATELESS
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return httpSecurity.build();
    }

    // 커스텀 로그인 필터를 위한 AuthenticationManager Bean 수동 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
