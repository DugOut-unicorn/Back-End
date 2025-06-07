package dugout.DugOut.web.dto.response;

import lombok.Getter;

@Getter
public class ToggleMatchResponse {
    private final Long matchingPostIdx;
    private final boolean isMatched;

    public ToggleMatchResponse(Long matchingPostIdx, boolean isMatched) {
        this.matchingPostIdx = matchingPostIdx;
        this.isMatched = isMatched;
    }
}