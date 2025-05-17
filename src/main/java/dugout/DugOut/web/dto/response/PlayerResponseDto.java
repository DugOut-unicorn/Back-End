package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerResponseDto {
    private Integer backNumber;
    private String playerName;
    private String playerImageUrl;
} 