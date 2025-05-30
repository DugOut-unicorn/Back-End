package dugout.DugOut.config;

import dugout.DugOut.domain.User;
import dugout.DugOut.repository.UserRepository;
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
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    public WebSocketConfig(JwtService jwtService,
                           UserRepository userRepository) {
        this.jwtService = jwtService;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue");
        registry.setUserDestinationPrefix("/user");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request,
                                                      WebSocketHandler wsHandler,
                                                      Map<String,Object> attrs) {
                        // ① URL 쿼리 우선
                        List<String> params = UriComponentsBuilder.fromUri(request.getURI())
                                .build().getQueryParams()
                                .get("token");
                        String raw = params != null && !params.isEmpty()
                                ? params.get(0)
                                : request.getHeaders().getFirst("Authorization");

                        if (raw != null) {
                            // "Bearer " 가 붙었든 안 붙었든 strip
                            String jwt = raw.startsWith("Bearer ")
                                    ? raw.substring(7)
                                    : raw;
                            Integer userId = jwtService.getUserIdFromToken(jwt);
                            return userId::toString;
                        }
                        return () -> "anonymous";
                    }

                })

                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                // 클라이언트가 CONNECT 프레임 보낼 때
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // STOMP CONNECT 헤더에서 Authorization 꺼내기
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String rawJwt = authHeader.substring(7);
                        Integer userId = jwtService.getUserIdFromToken(rawJwt);
                        // Principal 을 userId 로 설정
                        accessor.setUser(() -> userId.toString());
                        System.out.println("▶ CONNECT interceptor set Principal=" + userId);
                    }
                }
                return message;
            }
        });
    }

}
