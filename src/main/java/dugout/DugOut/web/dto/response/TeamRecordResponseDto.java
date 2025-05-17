package dugout.DugOut.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamRecordResponseDto {
    private Integer rank;        // 순위
    private Integer game;        // 경기수
    private Integer win;         // 승
    private Integer draw;        // 무
    private Integer lose;        // 패
    private Double winRate;      // 승률
} 