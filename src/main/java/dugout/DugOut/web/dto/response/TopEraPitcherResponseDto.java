package dugout.DugOut.web.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopEraPitcherResponseDto {
    private String playerName;
    private Integer backNumber;
    private Double era;
    private String playerImageUrl;

    public TopEraPitcherResponseDto(String playerName, Integer backNumber, Double era, String playerImageUrl) {
        this.playerName = playerName;
        this.backNumber = backNumber;
        this.era = era;
        this.playerImageUrl = playerImageUrl;
    }
} 