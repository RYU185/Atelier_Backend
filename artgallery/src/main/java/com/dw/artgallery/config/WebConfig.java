package com.dw.artgallery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://192.168.0.77:81")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(true)
                .maxAge(3600);  // preflight 요청 캐시 시간 설정
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // user.dir + 상대경로를 절대경로로 변환
        String absolutePath = Paths.get(System.getProperty("user.dir"), uploadDir).toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absolutePath);
    }
}
