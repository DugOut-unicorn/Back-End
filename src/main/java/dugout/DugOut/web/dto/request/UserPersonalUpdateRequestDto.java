package dugout.DugOut.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserPersonalUpdateRequestDto {
    @NotNull(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하여야 합니다.")
    private String nickname;

    @Size(max = 500, message = "자기소개는 500자 이하여야 합니다.")
    private String bio;

    @NotNull(message = "응원팀은 필수 입력값입니다.")
    private Integer cheeringTeamId;
} 