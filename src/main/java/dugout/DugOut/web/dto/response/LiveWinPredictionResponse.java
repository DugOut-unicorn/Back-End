package dugout.DugOut.web.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class LiveWinPredictionResponse {
    private final String gameId;
    private final String awayTeam;
    private final String homeTeam;
    private final Integer inning;
    private final Float winProbability;
    private final Integer homeAccumScore;
    private final Integer awayAccumScore;
    private final LocalDateTime predictedAt;

    public LiveWinPredictionResponse(String gameId,
                                     String awayTeam,
                                     String homeTeam,
                                     Integer inning,
                                     Float winProbability,
                                     Integer homeAccumScore,
                                     Integer awayAccumScore,
                                     LocalDateTime predictedAt) {
        this.gameId = gameId;
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.inning = inning;
        this.winProbability = winProbability;
        this.homeAccumScore = homeAccumScore;
        this.awayAccumScore = awayAccumScore;
        this.predictedAt = predictedAt;
    }
}
