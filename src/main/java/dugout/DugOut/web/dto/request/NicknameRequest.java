package dugout.DugOut.web.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NicknameRequest {
    private Integer userIdx;
    private String nickname;
    private Integer cheeringTeamId;
} 