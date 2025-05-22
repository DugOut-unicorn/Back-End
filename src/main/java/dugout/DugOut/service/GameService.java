package dugout.DugOut.service;

import dugout.DugOut.domain.Game;
import dugout.DugOut.domain.enums.Stadium;
import dugout.DugOut.domain.enums.Team;
import dugout.DugOut.repository.GameRepository;
import dugout.DugOut.web.dto.response.GameListResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Service
public class GameService {
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;

    }

    /**
     * 지정된 날짜(day)와 현재 시각(now) 기준으로
     * 이미 시작했거나 아직 종료되지 않은(<= now) '진행 중'인 경기만 조회
     *
     * @param day 날짜 (yyyy-MM-dd)
     * @param now 현재 시각 (HH:mm)
     */
    public List<Game> getOngoingGames(LocalDate day, LocalTime now) {
        LocalDateTime startOfDay = day.atStartOfDay();
        // 과거 날짜면 endOfDay, 오늘이면 now
        LocalDate today = LocalDate.now();
        LocalDateTime endOfDayOrNow = day.isEqual(today)
                ? LocalDateTime.of(day, now)
                : day.atTime(LocalTime.MAX);
        return gameRepository.findOngoingByPeriod(startOfDay, endOfDayOrNow);
    }

    /**
     * 지정된 날짜에 해당하는 모든 경기 조회
     *
     * @param date 날짜 (yyyy-MM-dd)
     */
    public List<GameListResponse.GameDto> getGamesByDate(LocalDate date) {

        List<Game> games = gameRepository.findGamesByDate(date);


        return games.stream()
                .map(game -> {
                    // 1) Team enum 에서 팀 이름 조회
                    String homeName = Team.getNameByIdx(game.getHomeTeamIdx());
                    String awayName = Team.getNameByIdx(game.getAwayTeamIdx());

                    // 2) Stadium enum 에서 구장 이름 조회
                    String stadiumName = Stadium.getNameByIdx(game.getStadiumIdx());

                    // 3) startTime(int) → "HH:mm" 포맷
                    int raw = game.getStartTime();        // ex. 1300
                    String startTime = String.format("%02d:%02d", raw / 100, raw % 100);

                    return new GameListResponse.GameDto(
                            game.getGameIdx(),
                            homeName,
                            awayName,
                            stadiumName,
                            startTime
                    );
                })
                .toList();
    }
}
