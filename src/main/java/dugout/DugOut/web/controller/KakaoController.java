package dugout.DugOut.web.controller;

import dugout.DugOut.service.KakaoService;
import dugout.DugOut.web.dto.response.KakaoUserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

    @PostMapping("/login")
    public ResponseEntity<KakaoUserInfoResponseDto> kakaoLogin(@RequestHeader("Authorization") String accessToken) {
        // "Bearer " 접두사 제거
        String token = accessToken.replace("Bearer ", "");
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(token);
        return ResponseEntity.ok(userInfo);
    }
} 