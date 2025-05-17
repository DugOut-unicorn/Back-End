package dugout.DugOut.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
@Schema(description = "응원팀 수정 요청")
public class CheeringTeamUpdateRequest {
    
    @NotNull(message = "응원팀은 필수 선택값입니다.")
    @Min(value = 1, message = "응원팀 ID는 1에서 10 사이의 값이어야 합니다.")
    @Max(value = 10, message = "응원팀 ID는 1에서 10 사이의 값이어야 합니다.")
    @Schema(description = "응원팀 ID", example = "1")
    private Integer cheeringTeamId;
} 