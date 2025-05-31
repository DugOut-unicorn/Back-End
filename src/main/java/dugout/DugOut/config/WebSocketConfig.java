package dugout.DugOut.config;

import dugout.DugOut.service.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
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
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    public WebSocketConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws-chat")
//                .setAllowedOriginPatterns("*")
//                .setHandshakeHandler(new DefaultHandshakeHandler() {
//                    @Override
//                    protected Principal determineUser(ServerHttpRequest request,
//                                                      WebSocketHandler wsHandler,
//                                                      Map<String, Object> attributes) {
//                        // ① URL 쿼리 또는 헤더에서 토큰 꺼내기
////                        String raw = UriComponentsBuilder.fromUri(request.getURI())
////                                .build().getQueryParams().getFirst("token");
////                        if (raw == null) {
////                            raw = request.getHeaders().getFirst("Authorization");
////                        }
////                        if (raw != null) {
////                            String jwt = raw.startsWith("Bearer ")
////                                    ? raw.substring(7)
////                                    : raw;
////                            // ② 토큰에서 email(subject) 추출
////                            String email = jwtService.getEmailFromToken(jwt);
////                            // ③ Principal.name 으로 email 사용
////                            return () -> email;
////                        }
////                        return () -> "anonymous";
//                        // ① 헤더에서 토큰 꺼내기
//                        String raw = request.getHeaders().getFirst("Authorization");
//                        if (raw != null && raw.startsWith("Bearer ")) {
//                            String jwt = raw.substring(7);
//                            try {
//                                String email = jwtService.getEmailFromToken(jwt);
//                                return () -> email;
//                            } catch (Exception e) {
//                                // 토큰 파싱 실패 시 로그 남기고
//                                System.out.println("[Handshake] 잘못된 토큰: " + e.getMessage());
//                            }
//                        }
//                        return () -> "anonymous";
//                    }
//                })
//                .withSockJS();
//    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue")
                .setHeartbeatValue(new long[]{4000, 4000});
        registry.setUserDestinationPrefix("/user");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null) {
                    // ① STOMP CONNECT 프레임일 때 토큰 파싱
                    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                        // STOMP 헤더의 "Authorization" (Native Header) 가져오기
                        List<String> authHeaders = accessor.getNativeHeader("Authorization");
                        if (authHeaders != null && !authHeaders.isEmpty()) {
                            String rawToken = authHeaders.get(0); // "Bearer <토큰>"
                            if (rawToken.startsWith("Bearer ")) {
                                String jwt = rawToken.substring(7);
                                try {
                                    String email = jwtService.getEmailFromToken(jwt);
                                    // Principal 객체를 만들어서 세션에 붙여 줌
                                    Principal user = () -> email;
                                    accessor.setUser(user);
                                    System.out.println("[STOMP CONNECT] 인증 성공, email=" + email);
                                } catch (Exception e) {
                                    System.out.println("[STOMP CONNECT] 인증 실패: " + e.getMessage());
                                    // 인증 실패 시, 연결 자체를 차단하려면 null 반환
                                    return null;
                                }
                            }
                        } else {
                            System.out.println("[STOMP CONNECT] Authorization 헤더가 없음");
                            return null;  // 헤더 없으면 연결 거부
                        }
                    }
                }
                return message;
            }
        });
    }
}
