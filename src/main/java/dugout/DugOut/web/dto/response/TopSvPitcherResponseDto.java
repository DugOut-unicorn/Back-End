package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopSvPitcherResponseDto {
    private String playerName;
    private Integer sv;
    private String playerImageUrl;
} 