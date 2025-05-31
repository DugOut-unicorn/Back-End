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

//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws-chat")
//                .setAllowedOriginPatterns("*")
//                .setHandshakeHandler(new DefaultHandshakeHandler() {
//                    @Override
//                    protected Principal determineUser(ServerHttpRequest request,
//                                                      WebSocketHandler wsHandler,
//                                                      Map<String, Object> attributes) {
//                        // ① 쿼리 파라미터에서 토큰 가져오기
//                        String token = UriComponentsBuilder
//                                .fromUri(request.getURI())
//                                .build()
//                                .getQueryParams()
//                                .getFirst("token");
//                        if (token != null) {
//                            try {
//                                String email = jwtService.getEmailFromToken(token);
//                                return () -> email;   // Principal.name = email
//                            } catch (Exception e) {
//                                System.out.println("[Handshake] 토큰 파싱 실패: " + e.getMessage());
//                            }
//                        }
//                        return () -> "anonymous";
//                    }
//                })
//                .withSockJS();
//
//    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")

                // (1) SockJS 초기 요청 '/ws-chat/info?token=...' 단계에서 'token' 파싱해 attributes에 저장
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request,
                                                   ServerHttpResponse response,
                                                   WebSocketHandler wsHandler,
                                                   Map<String, Object> attributes) {
                        // 쿼리 파라미터에서 token 꺼내기
                        String token = UriComponentsBuilder
                                .fromUri(request.getURI())
                                .build()
                                .getQueryParams()
                                .getFirst("token");

                        if (token != null) {
                            attributes.put("token", token);
                            System.out.println("[HandshakeInterceptor] token 저장: " + token);
                        }
                        // true를 반환해야 다음 핸드셰이크 단계로 진행됩니다.
                        return true;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request,
                                               ServerHttpResponse response,
                                               WebSocketHandler wsHandler,
                                               Exception exception) {
                        // 보통 이곳에서는 별도 작업할 것 없습니다.
                    }
                })

                // (2) 최종 Upgrade(웹소켓 핸드셰이크) 시점에서 attributes에 저장된 token을 꺼내서 Principal 설정
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request,
                                                      WebSocketHandler wsHandler,
                                                      Map<String, Object> attributes) {
                        Object tokenObj = attributes.get("token");
                        if (tokenObj instanceof String) {
                            String token = (String) tokenObj;
                            try {
                                String email = jwtService.getEmailFromToken(token);
                                System.out.println("[Handshake] JWT 파싱 성공, email=" + email);
                                return () -> email; // Principal.name = email
                            } catch (Exception e) {
                                System.out.println("[Handshake] 토큰 파싱 실패: " + e.getMessage());
                            }
                        }
                        // 토큰이 없거나 파싱 실패 시 anonymous
                        System.out.println("[Handshake] token이 없거나 파싱 실패, anonymous 처리");
                        return () -> "anonymous";
                    }
                })
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue")
                .setHeartbeatValue(new long[]{4000, 4000});
        registry.setUserDestinationPrefix("/user");
        registry.setApplicationDestinationPrefixes("/app");
    }

}
