package dugout.DugOut.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "닉네임 수정 요청")
public class NicknameRequest {
    
    @Schema(description = "사용자 ID", example = "1")
    private Integer userIdx;
    
    @Schema(description = "새로운 닉네임", example = "새로운닉네임")
    private String nickname;
    
    @Schema(description = "응원팀 ID", example = "1")
    private Integer cheeringTeamId;
} 