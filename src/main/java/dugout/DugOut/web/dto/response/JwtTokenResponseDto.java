package dugout.DugOut.web.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JwtTokenResponseDto {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;

    public JwtTokenResponseDto(String accessToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
    }
} 