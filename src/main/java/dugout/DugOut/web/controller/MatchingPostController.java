package dugout.DugOut.web.controller;

import dugout.DugOut.domain.Game;
import dugout.DugOut.domain.MatchingPost;
import dugout.DugOut.domain.User;
import dugout.DugOut.dto.MatchingPostResponseDto;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.service.GameService;
import dugout.DugOut.service.JwtService;
import dugout.DugOut.service.MatchingPostService;
import dugout.DugOut.web.dto.request.CreateMatchingPostRequest;
import dugout.DugOut.web.dto.response.CreateMatchingPostResponse;
import dugout.DugOut.web.dto.response.GameListResponse;
import dugout.DugOut.web.dto.response.MatchingPostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/matching-post")
@RequiredArgsConstructor
@Tag(name = "/matching-post", description = "직관 매칭 API")
public class MatchingPostController {

    private final GameService gameService;
    private final MatchingPostService matchingPostService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        return bearerToken.substring(7);
    }

    private User getUserFromToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        String email = jwtService.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }


    @Operation(summary = "날짜별 경기 조회", description = "쿼리 파라미터로 받은 날짜에 해당하는 경기 리스트를 반환합니다.")
    @GetMapping("/games")
    public ResponseEntity<GameListResponse> getGamesByDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Service 에서 GameDto 리스트를 받아옵니다.
        List<GameListResponse.GameDto> games = gameService.getGamesByDate(date);

        // 최종 응답 DTO 생성
        GameListResponse response = new GameListResponse(date, games);
        return ResponseEntity.ok(response);
    }


//    @Operation(
//            summary = "매칭 글 등록",
//            description = "매칭글을 등록합니다.",
//            parameters = {
//                    @Parameter(
//                            name = "Authorization",
//                            description = "JWT 토큰",
//                            required = true,
//                            in = ParameterIn.HEADER,
//                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
//                    )
//            }
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode="201", description="등록 성공"),
//            @ApiResponse(responseCode="400", description="잘못된 요청 (검증 실패)"),
//            @ApiResponse(responseCode="401", description="인증 필요"),
//    })
//    @PostMapping
//    public ResponseEntity<CreateMatchingPostResponse> create(HttpServletRequest request, @Valid @RequestBody CreateMatchingPostRequest req) {
//        User user = getUserFromToken(request);
//
//        Long newPostId = matchingPostService.create(req, user);
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(newPostId)
//                .toUri();
//        return ResponseEntity
//                .created(location)
//                .body(new CreateMatchingPostResponse(newPostId));
//    }

}
