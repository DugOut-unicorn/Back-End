package dugout.DugOut.web.dto.response;

import dugout.DugOut.domain.MatchingPost;
import dugout.DugOut.domain.User;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class MatchingPostDetailResponse {
    private final Long   matchingPostIdx;
    private final Integer    userIdx;
    private final String  authorNickname;
    private final String  title;
    private final String  context;
    private final Boolean haveTicket;
    private final LocalDateTime createdAt;

    public MatchingPostDetailResponse(MatchingPost post, String authorNickname) {
        this.matchingPostIdx    = post.getMatchingPostIdx();
        this.userIdx            = post.getUserIdx();
        this.authorNickname     = authorNickname;
        this.title              = post.getTitle();
        this.context            = post.getContext();
        this.haveTicket         = post.getHaveTicket();
        this.createdAt          = post.getCreatedAt();
    }
}
