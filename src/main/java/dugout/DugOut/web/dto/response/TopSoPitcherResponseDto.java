package dugout.DugOut.web.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopSoPitcherResponseDto {
    private String playerName;
    private Integer backNumber;
    private Integer so;
    private String playerImageUrl;

    public TopSoPitcherResponseDto(String playerName, Integer backNumber, Integer so, String playerImageUrl) {
        this.playerName = playerName;
        this.backNumber = backNumber;
        this.so = so;
        this.playerImageUrl = playerImageUrl;
    }
} 