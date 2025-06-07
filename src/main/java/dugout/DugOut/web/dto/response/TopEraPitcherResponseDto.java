package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopEraPitcherResponseDto {
    private String playerName;
    private Integer backNumber;
    private Double era;
    private String playerImageUrl;
    private Integer playerIdx;
} 