package dugout.DugOut.repository;

import dugout.DugOut.domain.GameResult;
import dugout.DugOut.web.dto.response.RecentResultDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {

    @Query(value =
            "SELECT DISTINCT DATE(g.date) AS dt " +
                    "  FROM game_result gr " +
                    "  JOIN game g ON gr.game_idx = g.game_idx " +
                    " WHERE DATE(g.date) BETWEEN :lower AND :upper " +
                    " ORDER BY dt DESC " +
                    " LIMIT 1",
            nativeQuery = true)
    java.sql.Date findMostRecentMatchDate(LocalDate lower, LocalDate upper);


    @Query(value =
            "SELECT " +
                    "  g.game_idx      AS gameIdx, " +
                    "  g.home_team_idx AS homeTeamIdx, " +
                    "  g.away_team_idx AS awayTeamIdx, " +
                    "  gr.home_score   AS homeScore, " +
                    "  gr.away_score   AS awayScore, " +
                    "  g.date          AS scheduledAt, " +
                    "  gr.recorded_at  AS recordedAt " +
                    "FROM game_result gr " +
                    "  JOIN game g ON gr.game_idx = g.game_idx " +
                    "WHERE DATE(g.date) = :matchDate " +
                    "ORDER BY g.date ASC " +
                    "LIMIT :limit",
            nativeQuery = true)
    List<RecentResultDto> findRecentResultDtosByMatchDate(
            LocalDate matchDate,
            int limit
    );


    boolean existsByGameIdx(Integer gameIdx);
}

