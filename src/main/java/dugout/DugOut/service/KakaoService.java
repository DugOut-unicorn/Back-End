package dugout.DugOut.service;

import dugout.DugOut.domain.User;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.web.dto.response.KakaoTokenResponseDto;
import dugout.DugOut.web.dto.response.KakaoUserInfoResponseDto;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class KakaoService {

    private String clientId;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;
    private final UserRepository userRepository;

    @Autowired
    public KakaoService(@Value("${kakao.client_id}") String clientId, UserRepository userRepository) {
        this.clientId = clientId;
        this.userRepository = userRepository;
        KAUTH_TOKEN_URL_HOST ="https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    }

    public String getAccessTokenFromKakao(String code) {
        WebClient webClient = WebClient.builder()
                .baseUrl(KAUTH_TOKEN_URL_HOST)
                .build();

        KakaoTokenResponseDto kakaoTokenResponseDto = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());
        //제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
        log.info(" [Kakao Service] Id Token ------> {}", kakaoTokenResponseDto.getIdToken());
        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDto.getScope());

        // 액세스 토큰으로 사용자 정보 조회
        getUserInfo(kakaoTokenResponseDto.getAccessToken());

        return kakaoTokenResponseDto.getAccessToken();
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        log.info("[ Kakao Service ] Getting user info with access token: {}", accessToken);

        KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        log.info("[ Kakao Service ] Auth ID ---> {} ", userInfo.getId());
        log.info("[ Kakao Service ] Name (from properties) ---> {} ", userInfo.getProperties().get("nickname"));
        log.info("[ Kakao Service ] Nickname (from profile) ---> {} ", userInfo.getKakaoAccount().getProfile().getNickName());
        log.info("[ Kakao Service ] Email ---> {} ", userInfo.getKakaoAccount().getEmail());

        // 사용자 정보를 DB에 저장
        saveOrUpdateUser(userInfo);

        return userInfo;
    }

    private void saveOrUpdateUser(KakaoUserInfoResponseDto userInfo) {
        String email = userInfo.getKakaoAccount().getEmail();
        String nickname = userInfo.getKakaoAccount().getProfile().getNickName();
        String name = userInfo.getProperties().get("nickname");

        log.info("[ Kakao Service ] Attempting to save/update user with email: {}", email);
        log.info("[ Kakao Service ] User details - name: {}, nickname: {}", name, nickname);

        try {
            userRepository.findByEmail(email)
                    .ifPresentOrElse(
                            existingUser -> {
                                log.info("[ Kakao Service ] Found existing user: {}", existingUser);
                                existingUser.setName(name);
                                existingUser.setNickname(nickname);
                                User savedUser = userRepository.save(existingUser);
                                log.info("[ Kakao Service ] Updated existing user: {}", savedUser);
                            },
                            () -> {
                                User newUser = User.builder()
                                        .email(email)
                                        .name(name)
                                        .nickname(nickname)
                                        .build();
                                log.info("[ Kakao Service ] Creating new user: {}", newUser);
                                User savedUser = userRepository.save(newUser);
                                log.info("[ Kakao Service ] Created new user: {}", savedUser);
                            }
                    );
        } catch (Exception e) {
            log.error("[ Kakao Service ] Error saving user: {}", e.getMessage());
            throw e;
        }
    }
}
