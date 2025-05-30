package dugout.DugOut.config;

import dugout.DugOut.domain.User;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
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
                // 오직 JWT 헤더만 사용하도록 HandshakeHandler 설정
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request,
                                                      WebSocketHandler wsHandler,
                                                      Map<String, Object> attributes) {
                        if (!(request instanceof ServletServerHttpRequest servletReq)) {
                            throw new RuntimeException("Unsupported handshake request");
                        }
                        HttpServletRequest httpReq = servletReq.getServletRequest();

                        // 1) Authorization 또는 X-Authorization 헤더에서 Bearer 토큰 추출
                        String header = httpReq.getHeader("Authorization");
                        if (header == null) {
                            header = httpReq.getHeader("X-Authorization");
                        }
                        if (header == null || !header.startsWith("Bearer ")) {
                            throw new RuntimeException("유효하지 않은 토큰입니다.");
                        }

                        String jwt = header.substring(7);
                        // 2) JWT 검증 및 사용자 조회
                        String email = jwtService.getEmailFromToken(jwt);
                        User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                        System.out.println("WebSocket 인증 성공: userId=" + user.getUserIdx());
                        // 3) Principal.getName() 으로 userId 를 반환
                        return () -> String.valueOf(user.getUserIdx());
                    }
                })
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        String email = jwtService.getEmailFromToken(token.substring(7));
                        User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                        // 여기가 바로 STOMP 세션의 Principal 설정 지점
                        accessor.setUser(() -> String.valueOf(user.getUserIdx()));
                    }
                }
                return message;
            }
        });
    }
}
