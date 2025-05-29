package dugout.DugOut.web.dto.response;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MatchingPostListByGameResponse {
    private final Long matchingPostIdx;
    private final String title;
    private final String context;
    private final Boolean haveTicket;
    private final Boolean isMatched;
    private final LocalDateTime createdAt;

    public MatchingPostListByGameResponse(
            Long postIdx,
            String title,
            String context,
            Boolean haveTicket,
            Boolean isMatched,
            LocalDateTime createdAt
    ) {
        this.matchingPostIdx = postIdx;
        this.title           = title;
        this.context         = context;
        this.haveTicket      = haveTicket;
        this.isMatched       = isMatched;
        this.createdAt       = createdAt;
    }
}
