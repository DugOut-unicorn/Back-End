package dugout.DugOut.web.dto.response;

import dugout.DugOut.domain.MatchingPost;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class MatchingPostResponse {
    private final Integer postIdx;
    private final String title;
    private final Integer stadiumIdx;
    private final Integer gameIdx;
    private final String context;
    private final String userNickname;
    private final Integer userCheeringTeamId;
    private final Integer status;
    private final LocalDateTime createdAt;
    private final LocalDate preferredMatchDate;

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
            Integer userCheeringTeamId,
            Integer status,
            LocalDateTime createdAt,
            LocalDate preferredMatchDate
    ) {
        this.postIdx      = postIdx;
        this.title        = title;
        this.stadiumIdx   = stadiumIdx;
        this.gameIdx      = gameIdx;
        this.context      = context;
        this.userNickname = userNickname;
        this.userCheeringTeamId    = userCheeringTeamId;
        this.status       = status;
        this.createdAt    = createdAt;
        this.preferredMatchDate  = preferredMatchDate;
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
                e.getUser() != null ? e.getUser().getCheeringTeamId() : null,
                e.getStatus(),
                e.getCreatedAt(),
                e.getPreferredMatchDate()
        );
    }
}
