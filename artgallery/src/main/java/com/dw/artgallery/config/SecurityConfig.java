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
    private final SockJsTokenFilter sockJsTokenFilter;


    public SecurityConfig(TokenProvider tokenProvider,SockJsTokenFilter sockJsTokenFilter ) {
        this.tokenProvider = tokenProvider;
        this.sockJsTokenFilter= sockJsTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors().and()
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션을 상태 비유지로 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**", "/topic/**", "/queue/**").permitAll()  // WebSocket 경로는 인증 없이 허용
                        // Swagger 및 정적 리소스 경로는 인증 없이 허용
                        .requestMatchers("/*.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/user/login", "/api/user/register", "/api/user/logout").permitAll()  // 로그인, 회원가입, 로그아웃은 인증 없이 허용
                        .requestMatchers("/api/notices", "/api/notices/*", "/api/notices/search").permitAll()  // 공지사항 조회는 인증 없이 허용
                        .requestMatchers("/api/user/send-authcode", "/api/user/verify-authcode", "/api/user/reset-password").permitAll()
                        .requestMatchers("/api/user/check-id", "/api/user/check-email" ).permitAll()
                        // 아이디 찾기 API는 누구나 접근 가능 (인증 없이 접근)
                        .requestMatchers("/api/user/findid").permitAll()  // 수정된 경로: /api/users/find-id

                        // 공개 API
                        .requestMatchers("/api/art/**").permitAll()
                        .requestMatchers("/api/artist/**").permitAll()
                        .requestMatchers("/api/artistgallery/**").permitAll()
                        .requestMatchers("/api/usergallery/**").permitAll()
                        .requestMatchers("/api/community/**").permitAll()
                        .requestMatchers("/api/goods/**").permitAll()
                        .requestMatchers("/api/contacts").permitAll()

                        // 인증된 사용자 API
                        .requestMatchers("/api/comment/**").authenticated()
                        .requestMatchers("/api/drawing/**").authenticated()
                        .requestMatchers("/api/review/**").authenticated()
                        .requestMatchers("/api/ticket/**").authenticated()
                        .requestMatchers("/api/contacts/**").authenticated()
                        .requestMatchers("/api/user/me").authenticated()

                        // 유저 전용 API
                        .requestMatchers("/api/chat-room/**").hasRole("USER")
                        .requestMatchers("/api/cart/**").hasRole("USER")
                        .requestMatchers("/api/purchase/add").hasRole("USER")
                        .requestMatchers("/api/purchase/view").hasRole("USER")
                        .requestMatchers("/api/purchase/buy-now").hasRole("USER")

                        // 관리자 전용 API
                        .requestMatchers("/api/cart").hasRole("ADMIN")
                        .requestMatchers("/api/purchase/all", "/api/purchase/user/**").permitAll()
                        .requestMatchers("/api/user/**").hasRole("ADMIN")
                        .requestMatchers("/api/goods/admin").hasRole("ADMIN")
                        .requestMatchers("/api/reservation/admin/**").permitAll()
                        .requestMatchers("/api/purchase/admin/**").permitAll()
                        // 업로드 경로는 접근 금지
                        .requestMatchers("/uploads/**").permitAll()

                        // 기타 요청은 인증 필수
                        .anyRequest().authenticated()  // 인증이 필요한 요청
                )
                .addFilterBefore(sockJsTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // BCryptPasswordEncoder 빈 등록
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager 빈 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
