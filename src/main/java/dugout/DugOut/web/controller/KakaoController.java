package dugout.DugOut.web.controller;

import dugout.DugOut.dto.KakaoTokenRequestDto;
import dugout.DugOut.service.JwtService;
import dugout.DugOut.service.KakaoService;
import dugout.DugOut.web.dto.response.ApiResponse;
import dugout.DugOut.web.dto.response.JwtTokenResponseDto;
import dugout.DugOut.web.dto.response.KakaoUserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kakao")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;
    private final JwtService jwtService;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @PostMapping("/user-info")
    public ApiResponse<JwtTokenResponseDto> getKakaoUserInfo(@RequestBody KakaoTokenRequestDto requestDto) {
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(requestDto.getAccessToken());
        String jwtToken = jwtService.generateToken(userInfo.getKakaoAccount().getEmail());
        JwtTokenResponseDto tokenResponse = new JwtTokenResponseDto(jwtToken, jwtExpiration);
        return ApiResponse.success("카카오 로그인 성공", tokenResponse);
    }
} 