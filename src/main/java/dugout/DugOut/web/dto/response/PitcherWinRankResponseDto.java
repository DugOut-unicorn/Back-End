package dugout.DugOut.web.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PitcherWinRankResponseDto {
    private String playerName;
    private Integer backNumber;
    private Integer w;
    private String playerImageUrl;

    public PitcherWinRankResponseDto(String playerName, Integer backNumber, Integer w, String playerImageUrl) {
        this.playerName = playerName;
        this.backNumber = backNumber;
        this.w = w;
        this.playerImageUrl = playerImageUrl;
    }
} 