package dugout.DugOut.dto;

import dugout.DugOut.domain.User;
import lombok.Getter;

@Getter
public class UserTempResponseDto {
    private final Integer userIdx;
    private final String nickname;
    private final Integer cheeringTeamId;
    private final String bio;
    private final String profileImageUrl;
    private final Float userTemp;

    public UserTempResponseDto(User user) {
        this.userIdx = user.getUserIdx();
        this.nickname = user.getNickname();
        this.cheeringTeamId = user.getCheeringTeamId();
        this.bio = user.getBio();
        this.profileImageUrl = user.getProfileImageUrl();
        this.userTemp = user.getUserTemp();
    }
} 