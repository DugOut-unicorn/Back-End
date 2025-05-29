package dugout.DugOut.service;

import dugout.DugOut.domain.Game;
import dugout.DugOut.domain.Team;
import dugout.DugOut.domain.enums.Stadium;
import dugout.DugOut.repository.GameRepository;
import dugout.DugOut.repository.TeamRepository;
import dugout.DugOut.web.dto.response.CalendarGamesResponse;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CalendarService {
    private final GameRepository gameRepo;
    private final TeamRepository teamRepo;

    public CalendarService(GameRepository gameRepo, TeamRepository teamRepo) {
        this.gameRepo = gameRepo;
        this.teamRepo = teamRepo;
    }

    /**
     * 프론트에서 받은 YearMonth(yyyy-MM) 기준으로
     * 그 달에 잡힌 모든 경기(또는 optional day/cheeringTeamIdx에 해당하는 경기)
     * 날짜와 팀 이름, 구장 이름, 경기 날짜(문자열)까지 반환
     */
    public CalendarGamesResponse getMonthlyGames(
            YearMonth ym,
            @Nullable Integer dayOfMonth,
            @Nullable Integer cheeringTeamIdx
    ) {
        // 1) 조회 기간 계산
        LocalDateTime start, end;
        if (dayOfMonth != null) {
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

        // 3) cheeringTeamIdx 필터링
        if (cheeringTeamIdx != null) {
            games = games.stream()
                    .filter(g ->
                            cheeringTeamIdx.equals(g.getHomeTeamIdx()) ||
                                    cheeringTeamIdx.equals(g.getAwayTeamIdx())
                    )
                    .toList();
        }

        // 4) 팀 이름 매핑
        Set<Integer> teamIds = games.stream()
                .flatMap(g -> Stream.of(g.getHomeTeamIdx(), g.getAwayTeamIdx()))
                .collect(Collectors.toSet());
        Map<Integer, String> nameMap = teamRepo.findAllById(teamIds).stream()
                .collect(Collectors.toMap(Team::getTeamIdx, Team::getTeamName));

        // 5) 구장 이름 매핑 (enum)
        Map<Integer, String> stadiumNameMap = games.stream()
                .map(Game::getStadiumIdx)
                .distinct()
                .collect(Collectors.toMap(
                        idx -> idx,
                        Stadium::getNameByIdx
                ));

        // 6) 날짜별 DTO 생성
        Map<Integer, List<CalendarGamesResponse.GameDetailDto>> byDay = new TreeMap<>();
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Game g : games) {
            int day = g.getDate().toLocalDate().getDayOfMonth();

            String homeName    = nameMap.get(g.getHomeTeamIdx());
            String awayName    = nameMap.get(g.getAwayTeamIdx());
            String stadiumName = stadiumNameMap.get(g.getStadiumIdx());
            Integer startTime  = g.getStartTime();

            CalendarGamesResponse.GameDetailDto detail =
                    new CalendarGamesResponse.GameDetailDto(
                            g.getGameIdx(),
                            homeName,
                            awayName,
                            stadiumName,
                            startTime
                    );

            byDay.computeIfAbsent(day, d -> new ArrayList<>())
                    .add(detail);
        }

        // 7) 최종 응답 리스트로 변환
        List<CalendarGamesResponse.DayGamesDto> days = byDay.entrySet().stream()
                .map(e -> new CalendarGamesResponse.DayGamesDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new CalendarGamesResponse(
                ym.getYear(), ym.getMonthValue(), days
        );
    }
}
