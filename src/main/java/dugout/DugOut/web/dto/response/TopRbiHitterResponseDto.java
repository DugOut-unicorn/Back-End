package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopRbiHitterResponseDto {
    private String playerName;
    private Integer rbi;
    private String playerImageUrl;
} 