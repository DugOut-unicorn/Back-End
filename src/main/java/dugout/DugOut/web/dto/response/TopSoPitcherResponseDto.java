package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopSoPitcherResponseDto {
    private String playerName;
    private Integer so;
    private String playerImageUrl;
} 