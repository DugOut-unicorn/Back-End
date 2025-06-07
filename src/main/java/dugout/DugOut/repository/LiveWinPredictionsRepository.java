package dugout.DugOut.repository;


import dugout.DugOut.domain.LiveWinPrediction;
import dugout.DugOut.web.dto.LiveWinPredictionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LiveWinPredictionsRepository extends JpaRepository<LiveWinPrediction, Long> {

    @Query(value =
            "SELECT DATE(predicted_at) " +
                    "FROM live_win_predictions " +
                    "GROUP BY DATE(predicted_at) " +
                    "ORDER BY DATE(predicted_at) DESC " +
                    "LIMIT 1",
            nativeQuery = true)
    Date findLatestPredictionDate();

    @Query(value =
            "SELECT " +
                    " p.game_id          AS gameId, " +
                    " p.inning           AS inning, " +
                    " p.win_probability  AS winProbability, " +
                    " p.home_accum_score AS homeAccumScore, " +
                    " p.away_accum_score AS awayAccumScore, " +
                    " p.predicted_at     AS predictedAt " +
                    "FROM live_win_predictions p " +
                    "INNER JOIN ( " +
                    "  SELECT " +
                    "    game_id, " +
                    "    MAX(predicted_at) AS max_ts " +
                    "  FROM live_win_predictions " +
                    "  WHERE DATE(predicted_at) = :latestDate " +
                    "  GROUP BY game_id " +
                    ") AS latest_per_game " +
                    "  ON p.game_id = latest_per_game.game_id " +
                    " AND p.predicted_at = latest_per_game.max_ts " +
                    "WHERE DATE(p.predicted_at) = :latestDate " +
                    "ORDER BY p.game_id",
            nativeQuery = true)
    List<LiveWinPredictionDto> findLatestPredictionsByDate(
            @Param("latestDate") Date latestDate
    );
}
