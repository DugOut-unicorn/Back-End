package dugout.DugOut.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "닉네임 수정 요청")
public class NicknameUpdateRequest {
    
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하여야 합니다.")
    @Schema(description = "새로운 닉네임", example = "새로운닉네임")
    private String nickname;
} 