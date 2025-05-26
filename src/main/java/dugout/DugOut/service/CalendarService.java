package dugout.DugOut.service;

import dugout.DugOut.domain.Game;
import dugout.DugOut.domain.Team;
import dugout.DugOut.repository.GameRepository;
import dugout.DugOut.repository.TeamRepository;
import dugout.DugOut.web.dto.response.CalendarGamesResponse;
import jakarta.annotation.Nullable;
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
     * 그 달에 잡힌 모든 경기(또는 optional day/cheeringTeamIdx에 해당하는 경기) 날짜와 팀 이름을 반환
     */
    public CalendarGamesResponse getMonthlyGames(
            YearMonth ym,
            @Nullable Integer dayOfMonth,
            @Nullable Integer cheeringTeamIdx
    ) {
        // 1) 조회 기간 계산
        LocalDateTime start;
        LocalDateTime end;
        if (dayOfMonth != null) {
            // day 유효성 검사
            if (dayOfMonth < 1 || dayOfMonth > ym.lengthOfMonth()) {
                throw new IllegalArgumentException(
                        String.format("Invalid dayOfMonth: %d for YearMonth %s", dayOfMonth, ym)
                );
            }
            start = ym.atDay(dayOfMonth).atStartOfDay();
            end   = start.plusDays(1);
        } else {
            start = ym.atDay(1).atStartOfDay();
            end   = ym.plusMonths(1).atDay(1).atStartOfDay();
        }

        // 2) 기간 내 Game 엔티티 로드
        List<Game> games = gameRepo.findGamesByPeriod(start, end);

        // 3) cheeringTeamIdx가 있으면 해당 팀의 홈/어웨이 경기만 필터링
        if (cheeringTeamIdx != null) {
            games = games.stream()
                    .filter(g ->
                            cheeringTeamIdx.equals(g.getHomeTeamIdx()) ||
                                    cheeringTeamIdx.equals(g.getAwayTeamIdx())
                    )
                    .collect(Collectors.toList());
        }

        // 4) 팀 이름 매핑 한 번에 가져오기
        Set<Integer> teamIds = games.stream()
                .flatMap(g -> Stream.of(g.getHomeTeamIdx(), g.getAwayTeamIdx()))
                .collect(Collectors.toSet());
        Map<Integer, String> nameMap = teamRepo.findAllById(teamIds).stream()
                .collect(Collectors.toMap(Team::getTeamIdx, Team::getTeamName));

        // 5) dayOfMonth(또는 모든 날짜) 별 그룹핑 & DTO 변환
        Map<Integer, List<CalendarGamesResponse.GameDetailDto>> byDay = new TreeMap<>();
        for (Game g : games) {
            int day = g.getDate().toLocalDate().getDayOfMonth();
            CalendarGamesResponse.GameDetailDto detail = new CalendarGamesResponse.GameDetailDto(
                    g.getGameIdx(),
                    g.getDate().toLocalDate(),
                    nameMap.get(g.getHomeTeamIdx()),
                    nameMap.get(g.getAwayTeamIdx()),
                    g.getStartTime()
            );
            byDay.computeIfAbsent(day, d -> new ArrayList<>())
                    .add(detail);
        }

        // 6) Map → List<DayGamesDto>
        List<CalendarGamesResponse.DayGamesDto> days = byDay.entrySet().stream()
                .map(e -> new CalendarGamesResponse.DayGamesDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new CalendarGamesResponse(
                ym.getYear(), ym.getMonthValue(), days
        );
    }


}
