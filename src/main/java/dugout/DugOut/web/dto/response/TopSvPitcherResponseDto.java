package dugout.DugOut.web.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopSvPitcherResponseDto {
    private String playerName;
    private Integer backNumber;
    private Integer sv;
    private String playerImageUrl;

    public TopSvPitcherResponseDto(String playerName, Integer backNumber, Integer sv, String playerImageUrl) {
        this.playerName = playerName;
        this.backNumber = backNumber;
        this.sv = sv;
        this.playerImageUrl = playerImageUrl;
    }
} 