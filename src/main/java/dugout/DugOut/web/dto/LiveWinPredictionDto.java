package dugout.DugOut.web.dto;


import java.time.LocalDateTime;

public interface LiveWinPredictionDto {
    String getGameId();
    Integer getInning();
    Float getWinProbability();
    Integer getHomeAccumScore();
    Integer getAwayAccumScore();
    LocalDateTime getPredictedAt();
}
