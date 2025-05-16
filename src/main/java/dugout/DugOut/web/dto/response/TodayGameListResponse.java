package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TodayGameListResponse {
    private final int homeTeamIdx;
    private final int awayTeamIdx;
    private final int stadiumIdx;
    private final int startTime;
}
