package dugout.DugOut.web.dto.response;

import dugout.DugOut.domain.MatchingPost;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MatchingPostResponse {
    private final Integer postIdx;
    private final String title;
    private final Integer stadiumIdx;
    private final Integer gameIdx;
    private final String context;
    private final String userNickname;
    private final Integer status;
    private final LocalDateTime createdAt;

    /**
     * JPQL projection 전용 생성자
     *  순서: postIdx, title, stadiumIdx, gameIdx, context, userNickname, status, createdAt
     */
    public MatchingPostResponse(
            Integer postIdx,
            String title,
            Integer stadiumIdx,
            Integer gameIdx,
            String context,
            String userNickname,
            Integer status,
            LocalDateTime createdAt
    ) {
        this.postIdx      = postIdx;
        this.title        = title;
        this.stadiumIdx   = stadiumIdx;
        this.gameIdx      = gameIdx;
        this.context      = context;
        this.userNickname = userNickname;
        this.status       = status;
        this.createdAt    = createdAt;
    }

    /** Entity → DTO 변환용 생성자 (기존) */
    public MatchingPostResponse(MatchingPost e) {
        this(
                e.getMatchingPostIdx(),
                e.getTitle(),
                e.getStadiumIdx(),
                e.getGameIdx(),
                e.getContext(),
                e.getUser() != null ? e.getUser().getNickname() : null,
                e.getStatus(),
                e.getCreatedAt()
        );
    }
}
