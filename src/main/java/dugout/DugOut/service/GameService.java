package dugout.DugOut.service;

import dugout.DugOut.domain.Game;
import dugout.DugOut.repository.GameRepository;
import dugout.DugOut.web.dto.response.TodayGameListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
     * 임의의 기간(start~end)에 포함된 모든 Game 엔티티를 조회
     *
     * @param start 시작 시각 (inclusive)
     * @param end   종료 시각 (inclusive)
     */
    public List<Game> getGamesInPeriod(LocalDateTime start, LocalDateTime end) {
        return gameRepository.findGamesByPeriod(start, end);
    }

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    public List<TodayGameListResponse> getTodayGames() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday    = today.atStartOfDay();
        LocalDateTime startOfTomorrow = today.plusDays(1).atStartOfDay();

        log.info("▶ 오늘 경기 조회 범위: {} 부터 {} 까지", startOfToday, startOfTomorrow);

        return gameRepository.findTodayGames(startOfToday, startOfTomorrow);
    }
}
