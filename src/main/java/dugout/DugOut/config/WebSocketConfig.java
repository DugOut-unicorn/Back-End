package dugout.DugOut.config;

import dugout.DugOut.service.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    public WebSocketConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                // 개발 중이므로 와일드카드(*)로 풀어주지만, 실제 운영 환경에서는 허용 도메인을 명시하세요.
                .setAllowedOriginPatterns("*")

                // 1) SockJS 초기 요청("/ws-chat/info?token=…") 단계에서 쿼리 파라미터 "token"을 읽어 attributes에 저장
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request,
                                                   ServerHttpResponse response,
                                                   WebSocketHandler wsHandler,
                                                   Map<String, Object> attributes) {
                        // URI에서 쿼리 파라미터로 붙어 온 토큰 가져오기
                        String token = UriComponentsBuilder
                                .fromUri(request.getURI())
                                .build()
                                .getQueryParams()
                                .getFirst("token");

                        System.out.println("[HandshakeInterceptor] beforeHandshake 호출, token=" + token);

                        if (token != null && !token.isBlank()) {
                            // attributes 맵에 token을 남겨 두면, 이후 DefaultHandshakeHandler 단계에서 꺼내쓸 수 있다.
                            attributes.put("token", token);
                        }
                        // true를 반환해야 다음 단계로 넘어간다.
                        return true;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request,
                                               ServerHttpResponse response,
                                               WebSocketHandler wsHandler,
                                               Exception exception) {
                        // 보통 딱히 추가 로직이 필요 없다. 디버그용으로 로그만 찍어 둔다.
                        System.out.println("[HandshakeInterceptor] afterHandshake 호출");
                    }
                })

                // 2) 실제 업그레이드(Upgrade) 요청 시점에서 attributes.get("token")을 꺼내 사용
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request,
                                                      WebSocketHandler wsHandler,
                                                      Map<String, Object> attributes) {
                        // HandshakeInterceptor가 저장해 준 token 꺼내기
                        Object tokenObj = attributes.get("token");
                        System.out.println("[Handshake] determineUser 호출됨, attributes='token'=" + tokenObj);

                        if (tokenObj instanceof String) {
                            String token = (String) tokenObj;
                            try {
                                // JWT에서 이메일(subject)을 추출
                                String email = jwtService.getEmailFromToken(token);
                                System.out.println("[Handshake] 토큰 파싱 성공, email=" + email);
                                // WebSocketSession.principal.name = email
                                return () -> email;
                            } catch (Exception e) {
                                System.out.println("[Handshake] JWT 파싱 오류: " + e.getMessage());
                            }
                        }

                        // 토큰이 없거나 파싱 실패 시 anonymous 처리
                        System.out.println("[Handshake] 토큰이 없거나 잘못되어 anonymous 처리");
                        return () -> "anonymous";
                    }
                })
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Simple Broker 활성화 (/queue), 스프링 애플리케이션 뿌리는 prefix (/app)
        registry.enableSimpleBroker("/queue");
        registry.setUserDestinationPrefix("/user");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
