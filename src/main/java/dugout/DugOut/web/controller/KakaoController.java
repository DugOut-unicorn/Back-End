package dugout.DugOut.web.controller;

import dugout.DugOut.dto.KakaoTokenRequestDto;
import dugout.DugOut.service.KakaoService;
import dugout.DugOut.web.dto.response.ApiResponse;
import dugout.DugOut.web.dto.response.KakaoUserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kakao")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    @PostMapping("/user-info")
    public ApiResponse<KakaoUserInfoResponseDto> getKakaoUserInfo(@RequestBody KakaoTokenRequestDto requestDto) {
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(requestDto.getAccessToken());
        return ApiResponse.success("카카오 사용자 정보 조회 성공", userInfo);
    }
} 