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

                            // 1) JWT 토큰 파싱 (기존 로직)
                            String token = httpReq.getHeader("Authorization");
                            if (token == null) {
                                token = httpReq.getHeader("X-Authorization");
                            }
                            if (token != null && token.startsWith("Bearer ")) {
                                try {
                                    String email = jwtService.getEmailFromToken(token.substring(7));
                                    User user = userRepository.findByEmail(email)
                                            .orElseThrow();
                                    System.out.println("WebSocket 인증 성공: userId=" + user.getUserIdx());
                                    return () -> String.valueOf(user.getUserIdx());
                                } catch (Exception e) {
                                    System.out.println("WebSocket 인증 실패: " + e.getMessage());
                                }
                            }

                            // 2) JWT 없으면 쿼리 파라미터 'user' 로 식별
                            String userParam = httpReq.getParameter("user");
                            if (userParam != null && !userParam.isBlank()) {
                                System.out.println("WebSocket handshake user-param: " + userParam);
                                return () -> userParam;
                            }
                        }

                        // 그 외엔 anonymous
                        return () -> "anonymous";
                    }

                })
                .withSockJS();
    }
}
