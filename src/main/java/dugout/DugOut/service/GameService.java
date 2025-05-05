package dugout.DugOut.service;

import dugout.DugOut.domain.Game;
import dugout.DugOut.repository.GameRepository;
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
        LocalDateTime startOfDay  = day.atStartOfDay();
        LocalDateTime nowDateTime = LocalDateTime.of(day, now);
        return gameRepository.findOngoingByPeriod(startOfDay, nowDateTime);
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
}
