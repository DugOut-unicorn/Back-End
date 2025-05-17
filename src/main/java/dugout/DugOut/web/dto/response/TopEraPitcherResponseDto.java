package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TopEraPitcherResponseDto {
    private String playerName;
    private BigDecimal era;
    private String playerImageUrl;
} 