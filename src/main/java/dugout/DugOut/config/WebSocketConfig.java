package dugout.DugOut.config;

import dugout.DugOut.domain.User;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public WebSocketConfig(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /**
     * 기존에 쓰시던 방식 그대로 유지
     */
    private User getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        token = token.substring(7);
        String email = jwtService.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue");
        registry.setUserDestinationPrefix("/user");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                // Handshake 시점에 Principal 설정
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request,
                                                      WebSocketHandler wsHandler,
                                                      Map<String, Object> attributes) {
                        if (request instanceof ServletServerHttpRequest servletReq) {
                            HttpServletRequest httpReq = servletReq.getServletRequest();
                            try {
                                // STOMP CONNECT 프레임의 헤더에서 토큰 확인
                                String token = null;

                                // 1. Authorization 헤더 확인
                                token = httpReq.getHeader("Authorization");

                                // 2. X-Authorization 헤더 확인
                                if (token == null) {
                                    token = httpReq.getHeader("X-Authorization");
                                }

                                // 3. 쿼리 파라미터 확인
                                if (token == null) {
                                    token = httpReq.getParameter("token");
                                }

                                if (token == null || !token.startsWith("Bearer ")) {
                                    System.out.println("토큰이 없거나 Bearer 형식이 아님: " + token);
                                    return () -> "anonymous";
                                }

                                token = token.substring(7);
                                String email = jwtService.getEmailFromToken(token);
                                User user = userRepository.findByEmail(email)
                                        .orElseThrow(() -> new RuntimeException("User not found"));

                                System.out.println("WebSocket 인증 성공: " + user.getUserIdx());
                                return () -> String.valueOf(user.getUserIdx());

                            } catch (Exception e) {
                                System.out.println("WebSocket 인증 실패: " + e.getMessage());
                                e.printStackTrace();
                                return () -> "anonymous";
                            }
                        }
                        return () -> "anonymous";
                    }
                })
                .withSockJS();
    }
}
