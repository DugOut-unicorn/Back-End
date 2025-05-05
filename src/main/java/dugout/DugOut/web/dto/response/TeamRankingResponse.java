package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TeamRankingResponse {
    private final String teamName;
    private final int game;
    private final int win;
    private final int draw;
    private final int lose;
}
