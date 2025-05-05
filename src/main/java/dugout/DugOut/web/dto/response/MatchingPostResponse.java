package dugout.DugOut.web.dto.response;

import dugout.DugOut.domain.MatchingPost;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MatchingPostResponse {
    private final Integer postIdx;
    private final String title;
    private final String context;
    private final LocalDateTime createdAt;
    private final Integer stadiumIdx;
    private final Integer gameIdx;
    private final Integer status;
    private final String userNickname;

    public MatchingPostResponse(MatchingPost e) {
        this.postIdx     = e.getMatchingPostIdx();
        this.title       = e.getTitle();
        this.context     = e.getContext();
        this.createdAt   = e.getCreatedAt();
        this.stadiumIdx  = e.getStadiumIdx();
        this.gameIdx     = e.getGameIdx();
        this.status      = e.getStatus();
        this.userNickname  = e.getUser().getNickname();
    }
}
