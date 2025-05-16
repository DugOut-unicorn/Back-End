package dugout.DugOut.service;

import dugout.DugOut.domain.Game;
import dugout.DugOut.domain.Team;
import dugout.DugOut.repository.GameRepository;
import dugout.DugOut.repository.TeamRepository;
import dugout.DugOut.web.dto.response.CalendarGamesResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CalendarService {
    private final GameRepository gameRepo;
    private final TeamRepository teamRepo;

    public CalendarService(GameRepository gameRepo, TeamRepository teamRepo) {
        this.teamRepo = teamRepo;
        this.gameRepo = gameRepo;
    }


    /**
     * 프론트에서 받은 YearMonth(yyyy-MM) 기준으로
     * 그 달에 잡힌 모든 경기 날짜와 팀 이름을 반환
     */
    public CalendarGamesResponse getMonthlyGames(YearMonth ym) {
        // 1) 기간 계산: 1일 0시부터 “다음 달 1일 0시” 직전까지
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end   = ym.plusMonths(1).atDay(1).atStartOfDay();

        // 2) 그 기간의 모든 Game 엔티티 로드
        List<Game> games = gameRepo.findGamesByPeriod(start, end);

        // 3) 필요한 team_idx 모아서 한 번만 DB 조회
        Set<Integer> teamIds = games.stream()
                .flatMap(g -> Stream.of(g.getHomeTeamIdx(), g.getAwayTeamIdx()))
                .collect(Collectors.toSet());
        Map<Integer, String> nameMap = teamRepo.findAllById(teamIds).stream()
                .collect(Collectors.toMap(Team::getTeamIdx, Team::getTeamName));

        // 4) dayOfMonth 별로 그룹핑하고 DTO 변환
        Map<Integer, List<CalendarGamesResponse.GameDetailDto>> byDay = new TreeMap<>();
        for (Game g : games) {
            int day = g.getDate().toLocalDate().getDayOfMonth();
            CalendarGamesResponse.GameDetailDto detail = new CalendarGamesResponse.GameDetailDto(
                    g.getGameIdx(),
                    nameMap.get(g.getHomeTeamIdx()),
                    nameMap.get(g.getAwayTeamIdx()),
                    g.getStartTime()
            );
            byDay.computeIfAbsent(day, d -> new ArrayList<>())
                    .add(detail);
        }

        // 5) Map → List<DayGamesDto>
        List<CalendarGamesResponse.DayGamesDto> days = byDay.entrySet().stream()
                .map(e -> new CalendarGamesResponse.DayGamesDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new CalendarGamesResponse(
                ym.getYear(), ym.getMonthValue(), days
        );
    }
}
