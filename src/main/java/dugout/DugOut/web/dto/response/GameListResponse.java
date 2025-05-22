package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class GameListResponse {
    private LocalDate date;
    private final List<GameDto> games;

    @Getter
    @AllArgsConstructor
    public static class GameDto {
        private final Integer gameIdx;
        private final String homeTeamName;
        private final String awayTeamName;
        private final String stadium;
        private final String startTime;
    }
}

