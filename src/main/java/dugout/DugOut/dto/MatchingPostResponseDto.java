package dugout.DugOut.dto;

import dugout.DugOut.domain.MatchingPost;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MatchingPostResponseDto {
    private final Long matchingPostIdx;
    private final Integer userIdx;
    private final Integer gameIdx;
    private final Integer stadiumIdx;
    private final Integer teamIdx;
    private final String title;
    private final String context;
    private final Boolean haveTicket;
    private final LocalDateTime createdAt;
    private final Integer status;
    private final Integer isMatched;

    public MatchingPostResponseDto(MatchingPost matchingPost) {
        this.matchingPostIdx = matchingPost.getMatchingPostIdx();
        this.userIdx = matchingPost.getUserIdx();
        this.gameIdx = matchingPost.getGameIdx();
        this.stadiumIdx = matchingPost.getStadiumIdx();
        this.teamIdx = matchingPost.getCheeringTeamIdx();
        this.title = matchingPost.getTitle();
        this.context = matchingPost.getContext();
        this.haveTicket = matchingPost.getHaveTicket();
        this.createdAt = matchingPost.getCreatedAt();
        this.status = matchingPost.getStatus();
        this.isMatched = matchingPost.getIsMatched();
    }
} 