package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopGameHitterResponseDto {
    private String playerName;
    private Integer game;
    private String playerImageUrl;
} 