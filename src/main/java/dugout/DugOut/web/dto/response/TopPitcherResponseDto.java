package dugout.DugOut.web.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TopPitcherResponseDto {
    private String playerName;
    private Integer backNumber;
    private Integer playerIdx;
    private String playerImageUrl;
    private Double value;

    @Builder
    public TopPitcherResponseDto(String playerName, Integer backNumber, Integer playerIdx, String playerImageUrl, Double value) {
        this.playerName = playerName;
        this.backNumber = backNumber;
        this.playerIdx = playerIdx;
        this.playerImageUrl = playerImageUrl;
        this.value = value;
    }
} 