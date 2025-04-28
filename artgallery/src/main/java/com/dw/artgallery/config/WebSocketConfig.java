package com.dw.artgallery.config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(customHandshakeHandler())
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns("http://localhost:5173", "http://192.168.0.77:81")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /topic, /user 를 브로커의 목적지로 설정합니다. (채널 주제설정)
        registry.enableSimpleBroker("/topic", "/queue");
        // 클라이언트에서 /user 로 시작하는 목적지를 구독하면, 이를 내부적으로 처리할 수 있도록 함. (개인에게 보내는 채팅에 사용한다)
        registry.setUserDestinationPrefix("/user");
        // 클라이언트가 보내는 메시지는 /app 으로 시작해야 함.
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> converters) {
        converters.add(new MappingJackson2MessageConverter());
        return false;
    }

    private DefaultHandshakeHandler customHandshakeHandler(){
        return new CustomHandshakeHandler();
    }

}
