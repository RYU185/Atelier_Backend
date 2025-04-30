package com.dw.artgallery.config;

import com.dw.artgallery.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final TokenProvider jwtTokenProvider;

    private String getTokenFromUri(ServerHttpRequest request) {
        String uri = request.getURI().toString();
        return UriComponentsBuilder.fromUriString(uri).build().getQueryParams().getFirst("token");
    }


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        // token 파싱 (ex: ws?token=JWT)
        String token = getTokenFromUri(request); // 구현 필요
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getAuthentication(token).getName();
            attributes.put("user", new UsernamePrincipal(username));
            return true; 
        } else {

            response.setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
            return false;
        }
    }
    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
        // 사용 안함
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public static class UsernamePrincipal implements Principal {
        private final String name;

        public UsernamePrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UsernamePrincipal that)) return false;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

}
