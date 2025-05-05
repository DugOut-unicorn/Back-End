package dugout.DugOut.dto;

import dugout.DugOut.domain.User;
import lombok.Getter;

@Getter
public class UserTempResponseDto {
    private final Integer userIdx;
    private final Float userTemp;
    private final Integer cheeringTeamId;

    public UserTempResponseDto(User user) {
        this.userIdx = user.getUserIdx();
        this.userTemp = user.getUserTemp();
        this.cheeringTeamId = user.getCheeringTeamId();
    }
} 
 