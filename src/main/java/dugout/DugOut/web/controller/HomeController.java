package dugout.DugOut.web.controller;

import dugout.DugOut.domain.User;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.service.*;
import dugout.DugOut.web.dto.StadiumWeatherDto;
import dugout.DugOut.web.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
@Tag(name = "/home", description = "홈 화면 API")
public class HomeController {
    private final UserService userService;
    private final TeamRankingService teamRankingService;
    private final NewsFetchService newsFetchService;
    private final GameService gameService;
    private final CalendarService calendarService;
    private final MatchingPostService matchingPostService;
    private final GameResultService gameResultService;
    private final WeatherService weatherService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private User getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtService.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Operation(
            summary = "상단 배너 정보 반환"
    )
    @GetMapping("/entry-banner")
    public ResponseEntity<EntryBannerResponse> getEntryBannerInfo(
            HttpServletRequest request
    ) {
        User user = getCurrentUser(request);
        Integer userIdx = user.getUserIdx();
        Integer cheeringTeamId = userService.getCheeringTeamId(userIdx);
        String nickname = user.getNickname();
        return ResponseEntity.ok(new EntryBannerResponse(cheeringTeamId,nickname));
    }


    @Operation(
            summary = "뉴스 크롤링 트리거"
    )
    @GetMapping("/news-fetch")
    public ResponseEntity<List<NewsResponse>> triggerFetchAndReturn(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) throws IOException {
        // 파라미터 없으면 오늘, 있으면 해당 날짜 사용
        LocalDate target = (date != null ? date : LocalDate.now());
        String isoDate = target.format(DateTimeFormatter.ISO_DATE);

        List<NewsResponse> latest = newsFetchService.scrapeLatest(isoDate);
        return ResponseEntity.ok(latest);
    }


    @Operation(
            summary = "최신 매칭글 반환"
    )
    @GetMapping("/recent-matching-posts")
    public ResponseEntity<List<MatchingPostResponse>> getRecent() {
        List<MatchingPostResponse> dtoList = matchingPostService.getRecentPosts();
        return ResponseEntity.ok(dtoList);
    }


    @Operation(
            summary = "팀 순위 반환"
    )
    @GetMapping("/ranking")
    public ResponseEntity<List<TeamRankingResponse>> getRanking() {
        List<TeamRankingResponse> ranking = teamRankingService.getLatestRanking();
        return ResponseEntity.ok(ranking);
    }


    @Operation(
            summary = "월별/일별 경기 일정 반환"
    )
    @GetMapping("/calendar-games")
    public ResponseEntity<CalendarGamesResponse> getCalendarGames(
            @RequestParam("month")
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth ym,

            @RequestParam(value = "day", required = false) Integer dayOfMonth,

            @RequestParam(value = "cheeringTeamIdx", required = false) Integer cheeringTeamIdx
    ) {
        // day 파라미터 유효성 검사
        if (dayOfMonth != null) {
            if (dayOfMonth < 1 || dayOfMonth > ym.lengthOfMonth()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format("유효하지 않은 day 값: %d (월: %s)", dayOfMonth, ym)
                );
            }
        }

        // 2) cheeringTeamIdx 유효성 검사 (예: 양수만 허용)
        if (cheeringTeamIdx != null && cheeringTeamIdx < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "유효하지 않은 cheeringTeamIdx 값: " + cheeringTeamIdx
            );
        }

        // 3) 서비스 호출: month, dayOfMonth, cheeringTeamIdx 모두 넘김
        CalendarGamesResponse resp =
                calendarService.getMonthlyGames(ym, dayOfMonth, cheeringTeamIdx);

        return ResponseEntity.ok(resp);
    }


    @Operation(
            summary = "최근 경기 결과 반환"
    )
    @GetMapping("/recent-results")
    public ResponseEntity<GameResultResponse> recent(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(defaultValue = "5") int limit
    ) {
        LocalDate base = Optional.ofNullable(date)
                .orElse(LocalDate.now(ZoneId.of("Asia/Seoul")));
        GameResultResponse dto = gameResultService.getRecent(base, limit);
        return ResponseEntity.ok(dto);
    }


    @Operation(
            summary = "각 구장별 날씨 데이터 반환"
    )
    @GetMapping("/stadium-weathers")
    public List<StadiumWeatherDto> stadiumWeathers() {
        // WebFlux 가 아니라면 collectList().block() 로 동기화
        return weatherService.getAllStadiumWeathers()
                .collectList()
                .block();
    }
}
