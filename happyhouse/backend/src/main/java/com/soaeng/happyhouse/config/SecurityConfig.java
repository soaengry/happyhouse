package com.soaeng.happyhouse.config;

import com.soaeng.happyhouse.filter.JwtFilter;
import com.soaeng.happyhouse.handler.RefreshTokenLogoutHandler;
import com.soaeng.happyhouse.jwt.service.JwtService;
import com.soaeng.happyhouse.user.entity.RoleType;
import com.soaeng.happyhouse.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationSuccessHandler socialSuccessHandler;
    private final JwtService jwtService;
    private final JwtUtil jwtUtil;
    @Value("${server.host.front}")
    private String FRONT_HOST;
//    @Value("${spring.security.debug:false}")
//    boolean securityDebug;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .cors(corsCustomizer -> corsConfigurationSource())
                // CSRF 보안 필터 disable
                .csrf(AbstractHttpConfigurer::disable)
                // HTTP Basic 인증 방식 disable
                .httpBasic(AbstractHttpConfigurer::disable)
                // Form 로그인 방식 disable
                .formLogin(AbstractHttpConfigurer::disable)
                // oauth2
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(socialSuccessHandler))
                // 경로별 인가 작업
                .authorizeHttpRequests(auth -> auth
                        // 공개 API
                        .requestMatchers(HttpMethod.POST, "/user", "/user/exist").permitAll()
                        .requestMatchers("/jwt/exchange", "/jwt/refresh", "/error").permitAll()
                        .requestMatchers("/sido", "/gugun", "/dong", "/house/**", "/user/image").permitAll()
                        // 인증 필요 API
                        .requestMatchers(HttpMethod.GET, "/user").hasRole(RoleType.USER.name())
                        .requestMatchers(HttpMethod.PUT, "/user").hasRole(RoleType.USER.name())
                        .requestMatchers(HttpMethod.DELETE, "/user").hasRole(RoleType.USER.name())
                        .anyRequest().authenticated()
                )
                // Custom Filter 추가
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(FRONT_HOST));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // 권한 계층
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role(RoleType.ADMIN.name()).implies(RoleType.USER.name())
                .build();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.debug(securityDebug);
//    }
}
