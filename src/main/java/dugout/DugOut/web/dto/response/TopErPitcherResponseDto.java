package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopErPitcherResponseDto {
    private String playerName;
    private Integer er;
    private String playerImageUrl;
} 