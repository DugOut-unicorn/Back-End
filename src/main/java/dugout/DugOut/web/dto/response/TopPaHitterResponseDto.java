package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopPaHitterResponseDto {
    private String playerName;
    private Integer pa;
    private String playerImageUrl;
} 