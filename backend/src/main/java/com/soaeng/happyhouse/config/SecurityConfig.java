package com.soaeng.happyhouse.config;

import com.soaeng.happyhouse.filter.JwtFilter;
import com.soaeng.happyhouse.handler.RefreshTokenLogoutHandler;
import com.soaeng.happyhouse.jwt.service.JwtService;
import com.soaeng.happyhouse.user.entity.RoleType;
import com.soaeng.happyhouse.user.service.OAuthUserSyncService;
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
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final OAuthUserSyncService oAuthUserSyncService;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final JwtUtil jwtUtil;

    @Value("${server.host.front}")
    private String FRONT_HOST;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuthUserSyncService))
                        .successHandler(socialSuccessHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/user", "/user/exist").permitAll()
                        .requestMatchers("/jwt/exchange", "/jwt/refresh", "/error").permitAll()
                        .requestMatchers("/sido", "/gugun", "/dong", "/house/**", "/user/image").permitAll()
                        .requestMatchers(HttpMethod.GET, "/board", "/board/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/board", "/board/reply").hasRole(RoleType.USER.name())
                        .requestMatchers(HttpMethod.PUT, "/board/**").hasRole(RoleType.USER.name())
                        .requestMatchers(HttpMethod.DELETE, "/board/**").hasRole(RoleType.USER.name())
                        .requestMatchers(HttpMethod.GET, "/user").hasRole(RoleType.USER.name())
                        .requestMatchers(HttpMethod.PUT, "/user").hasRole(RoleType.USER.name())
                        .requestMatchers(HttpMethod.DELETE, "/user").hasRole(RoleType.USER.name())
                        .requestMatchers("/bookmark/**").hasRole(RoleType.USER.name())
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((req, res, ex) -> res.sendError(HttpServletResponse.SC_FORBIDDEN))
                )
                .logout(logout -> logout.addLogoutHandler(new RefreshTokenLogoutHandler(jwtUtil, jwtService)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return httpSecurity.build();
    }

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

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role(RoleType.ADMIN.name()).implies(RoleType.USER.name())
                .build();
    }
}
