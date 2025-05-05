package dugout.DugOut.web.controller;

import dugout.DugOut.domain.Game;
import dugout.DugOut.service.*;
import dugout.DugOut.web.dto.StadiumWeatherDto;
import dugout.DugOut.web.dto.response.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
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

    //로그인 한 유저의 응원 팀 반환
    @GetMapping("/users/cheering-team")
    public ResponseEntity<CheeringTeamResponse> getMyCheeringTeam(
            @RequestParam("userIdx") Integer userIdx
    ) {
        Integer cheeringTeamId = userService.getCheeringTeamId(userIdx);
        return ResponseEntity.ok(new CheeringTeamResponse(cheeringTeamId));
    }

//    //최신 뉴스 크롤링 후 반환
//    @GetMapping("/news-fetch")
//    public ResponseEntity<List<NewsResponse>> triggerFetchAndReturn() throws IOException {
//        // 오늘 날짜 ISO 포맷 (yyyy-MM-dd)
//        String todayIso = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
//        // Selenium 으로 바로 스크래핑한 결과
//        List<NewsResponse> latest = newsFetchService.scrapeWithSelenium(todayIso);
//        return ResponseEntity.ok(latest);
//    }

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

    //진행 중인 경기 조회
    @GetMapping("/ongoing-games")
    public ResponseEntity<List<Game>> getOngoing(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value="time", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time
    ) {
        LocalTime now = (time != null ? time : LocalTime.now());
        List<Game> ongoing = gameService.getOngoingGames(date, now);
        return ResponseEntity.ok(ongoing);
    }

    // 최신 5개 매칭글 반환
    @GetMapping("/recent-matching-posts")
    public ResponseEntity<List<MatchingPostResponse>> getRecent() {
        List<MatchingPostResponse> dtoList = matchingPostService.getRecentPosts();
        return ResponseEntity.ok(dtoList);
    }


    // 팀 랭킹 반환
    @GetMapping("/ranking")
    public ResponseEntity<List<TeamRankingResponse>> getRanking(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        List<TeamRankingResponse> ranking = teamRankingService.getRankingByDate(date);
        return ResponseEntity.ok(ranking);
    }

    //사용자가 응원하는 팀의 경기일 반환
    @GetMapping("/users/{userId}/calendar/days")
    public ResponseEntity<CalendarDateResponse> getGameDays(
            @PathVariable("userId") Integer userId,
            @RequestParam(value="month", required=false)
            @DateTimeFormat(pattern="yyyy-MM") YearMonth month
    ) {
        YearMonth ym = (month != null ? month : YearMonth.now());
        CalendarDateResponse resp = calendarService.getCalendarDays(userId, ym);
        return ResponseEntity.ok(resp);
    }

    // 최근 경기 결과 반환
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

    @GetMapping("/stadium-weathers")
    public List<StadiumWeatherDto> stadiumWeathers() {
        // WebFlux 가 아니라면 collectList().block() 로 동기화
        return weatherService.getAllStadiumWeathers()
                .collectList()
                .block();
    }
}
