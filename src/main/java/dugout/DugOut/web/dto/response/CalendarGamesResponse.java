package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CalendarGamesResponse {
    private final int year;
    private final int month;
    private final List<DayGamesDto> days;

    @Getter
    @AllArgsConstructor
    public static class DayGamesDto {
        private final Integer day;
        private final List<GameDetailDto> games;
    }

    @Getter
    @AllArgsConstructor
    public static class GameDetailDto {
        private final Integer gameIdx;
        private final String  homeTeamName;
        private final String  awayTeamName;
        private final Integer startTime;
    }
}
