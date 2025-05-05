package dugout.DugOut.web.dto.response;

import java.time.LocalDateTime;

public interface RecentResultDto {
    Integer getGameIdx();
    Integer getHomeTeamIdx();
    Integer getAwayTeamIdx();
    Integer getHomeScore();
    Integer getAwayScore();
    LocalDateTime getScheduledAt();
    LocalDateTime getRecordedAt();
}
