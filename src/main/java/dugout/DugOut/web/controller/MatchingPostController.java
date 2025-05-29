package dugout.DugOut.web.controller;

import dugout.DugOut.domain.User;
import dugout.DugOut.dto.MatchingPostResponseDto;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.service.GameService;
import dugout.DugOut.service.JwtService;
import dugout.DugOut.service.MatchingPostService;
import dugout.DugOut.web.dto.request.CreateMatchingPostRequest;
import dugout.DugOut.web.dto.response.CreateMatchingPostResponse;
import dugout.DugOut.web.dto.response.GameListResponse;
import dugout.DugOut.web.dto.response.MatchingPostDetailResponse;
import dugout.DugOut.web.dto.response.MatchingPostListByGameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/matching-post")
@RequiredArgsConstructor
@Tag(name = "/matching-post", description = "직관 매칭 API")
public class MatchingPostController {

    private final GameService gameService;
    private final MatchingPostService matchingPostService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private User getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtService.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

//    @Operation(summary = "날짜별 경기 조회", description = "쿼리 파라미터로 받은 날짜에 해당하는 경기 리스트를 반환합니다.")
//    @GetMapping("/games")
//    public ResponseEntity<GameListResponse> getGamesByDate(
//            @RequestParam
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//
//        // Service 에서 GameDto 리스트를 받아옵니다.
//        List<GameListResponse.GameDto> games = gameService.getGamesByDate(date);
//
//        // 최종 응답 DTO 생성
//        GameListResponse response = new GameListResponse(date, games);
//        return ResponseEntity.ok(response);
//    }

    @Operation(
            summary = "매칭 글 등록",
            description = "매칭글을 등록합니다."
    )
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CreateMatchingPostResponse> create(HttpServletRequest request, @Valid @RequestBody CreateMatchingPostRequest req) {
        User user = getCurrentUser(request);

        Long newPostId = matchingPostService.create(req, user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newPostId)
                .toUri();

        return ResponseEntity
                .created(location)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateMatchingPostResponse(newPostId));
    }

    @Operation(
            summary = "게임별 매칭글 조회 (DTO 반환)",
            description = "gameIdx 로 특정 경기의 매칭글 리스트를 간략화된 DTO 형태로 반환합니다."
    )
    @GetMapping("/by-game/{gameIdx}")
    public ResponseEntity<List<MatchingPostListByGameResponse>> getByGame(
            @PathVariable int gameIdx,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        List<MatchingPostListByGameResponse> dtos =
                matchingPostService.getPostsByGame(gameIdx, pageable);
        return ResponseEntity.ok(dtos);
    }

    @Operation(
            summary = "매칭글 상세 조회",
            description = "matchingPostIdx 로 특정 매칭글의 상세 정보를 반환합니다."
    )
    @GetMapping("/{postIdx}")
    public ResponseEntity<MatchingPostDetailResponse> getPostDetail(
            @PathVariable Long postIdx) {
        MatchingPostDetailResponse dto = matchingPostService.getPostDetail(postIdx);
        return ResponseEntity.ok(dto);
    }
}
