package dugout.DugOut.web.dto.response;


import dugout.DugOut.domain.MatchingPost;
import dugout.DugOut.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MypageMatchingPostsResponse {
    private final UserInfo user;
    private final List<PostInfo> posts;

    public MypageMatchingPostsResponse(UserInfo user, List<PostInfo> posts) {
        this.user  = user;
        this.posts = posts;
    }

    @Getter
    public static class UserInfo {
        private final String nickname;
        private final String profileImageUrl;
        private final Integer cheeringTeamId;

        public UserInfo(User u) {
            this.nickname        = u.getNickname();
            this.profileImageUrl = u.getProfileImageUrl();
            this.cheeringTeamId  = u.getCheeringTeamId();
        }
    }

    @Getter
    public static class PostInfo {
        private final Long   matchingPostIdx;
        private final String title;
        private final String context;
        private final Boolean haveTicket;
        private final Boolean isMatched;
        private final LocalDateTime createdAt;

        public PostInfo(MatchingPost p) {
            this.matchingPostIdx = p.getMatchingPostIdx();
            this.title           = p.getTitle();
            this.context         = p.getContext();
            this.haveTicket      = p.getHaveTicket();
            this.isMatched       = p.getIsMatched();
            this.createdAt       = p.getCreatedAt();
        }
    }
}
