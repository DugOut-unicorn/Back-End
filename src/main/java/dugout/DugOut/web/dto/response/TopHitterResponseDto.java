package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
public class TopHitterResponseDto {
    private String playerName;
    private Integer backNumber;
    private Integer playerIdx;
    private String playerImageUrl;
    private Double value;

    @Builder
    public TopHitterResponseDto(String playerName, Integer backNumber, Integer playerIdx, String playerImageUrl, Double value) {
        this.playerName = playerName;
        this.backNumber = backNumber;
        this.playerIdx = playerIdx;
        this.playerImageUrl = playerImageUrl;
        this.value = value;
    }
} 