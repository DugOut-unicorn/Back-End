package dugout.DugOut.web.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopWpctPitcherResponseDto {
    private String playerName;
    private Integer backNumber;
    private Double wpct;
    private String playerImageUrl;

    public TopWpctPitcherResponseDto(String playerName, Integer backNumber, Double wpct, String playerImageUrl) {
        this.playerName = playerName;
        this.backNumber = backNumber;
        this.wpct = wpct;
        this.playerImageUrl = playerImageUrl;
    }
} 