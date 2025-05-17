package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TopHitterResponseDto {
    private String playerName;
    private BigDecimal avg;
    private String playerImageUrl;
} 