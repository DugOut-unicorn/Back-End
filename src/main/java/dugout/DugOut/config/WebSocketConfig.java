package dugout.DugOut.config;

import dugout.DugOut.domain.User;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.service.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public WebSocketConfig(JwtService jwtService,
                           UserRepository userRepository) {
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
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request,
                                                      WebSocketHandler wsHandler,
                                                      Map<String, Object> attributes) {
                        // 1) URI 쿼리 파라미터에서 token=Bearer ... 꺼내기
                        MultiValueMap<String, String> params =
                                UriComponentsBuilder.fromUri(request.getURI())
                                        .build()
                                        .getQueryParams();
                        String tokenParam = params.getFirst("token");

                        if (tokenParam != null && tokenParam.startsWith("Bearer ")) {
                            String jwt = tokenParam.substring(7);
                            // 2) 이메일 파싱
                            String email = jwtService.getEmailFromToken(jwt);
                            // 3) DB 조회해서 userIdx 얻기
                            User user = userRepository.findByEmail(email)
                                    .orElseThrow(() -> new RuntimeException("User not found"));
                            // 4) userIdx.toString() 을 Principal.name 으로 사용
                            return () -> user.getUserIdx().toString();
                        }
                        return () -> "anonymous";
                    }
                })
                .withSockJS();
    }

    // 이전에 쓰던 CONNECT interceptor는 이제 필요 없습니다.
}
