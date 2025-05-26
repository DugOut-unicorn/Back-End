package dugout.DugOut.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class CreateMatchingPostRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String context;

    @NotNull
    private Boolean haveTicket;

    @NotNull
    private Integer gameIdx;
}
