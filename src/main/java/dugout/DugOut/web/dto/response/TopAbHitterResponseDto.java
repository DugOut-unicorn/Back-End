package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopAbHitterResponseDto {
    private String playerName;
    private Integer ab;
    private String playerImageUrl;
} 