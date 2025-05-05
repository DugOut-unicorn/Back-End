// src/main/java/dugout/DugOut/web/dto/response/GameResultResponse.java
package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
public class GameResultResponse {
    private LocalDate baseDate;
    private LocalDate matchDate;
    private List<RecentResultDto> results;
}
