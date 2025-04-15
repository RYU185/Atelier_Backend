package com.dw.artgallery.config;

import com.dw.artgallery.jwt.JwtFilter;
import com.dw.artgallery.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    public SecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**").permitAll()
                        // 정적 및 Swagger 리소스
                        .requestMatchers("/*.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 공개 API
                        .requestMatchers("/api/art/**").permitAll()
                        .requestMatchers("/api/artist/**").permitAll()
                        .requestMatchers("/api/artistgallery/**").permitAll()
                        .requestMatchers("/api/usergallery/**").permitAll()
                        .requestMatchers("/api/community/**").permitAll()
                        .requestMatchers("/api/goods/**").permitAll()
                        .requestMatchers("/api/notices/**").permitAll()
                        .requestMatchers("/api/contacts").permitAll()
                        .requestMatchers("/api/user/login", "/api/user/register", "/api/user/logout").permitAll()

                        // 인증된 사용자 API
                        .requestMatchers("/api/comment/**").authenticated()
                        .requestMatchers("/api/drawing/**").authenticated()
                        .requestMatchers("/api/review/**").authenticated()
                        .requestMatchers("/api/ticket/**").authenticated()
                        .requestMatchers("/api/contacts/**").authenticated()

                        // 유저 전용
                        .requestMatchers("/api/chat-room/**").hasRole("USER")
                        .requestMatchers("/api/cart/**").hasRole("USER")
                        .requestMatchers("/api/purchase/**").hasRole("USER")

                        // 관리자 전용
                        .requestMatchers("/api/cart").hasRole("ADMIN")
                        .requestMatchers("/api/purchase/all", "/api/purchase/user/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasRole("ADMIN")
                        .requestMatchers("/api/goods/admin").hasRole("ADMIN")

                        // 업로드 접근 금지
                        .requestMatchers("/uploads/**").denyAll()

                        // 기타 요청은 인증 필수
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
