package dugout.DugOut.web.dto.response;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRoomResponse {
    private final Integer chatRoomIdx;
    private final Integer peerIdx;
    private final String peerNickname;
    private final String peerProfileImageUrl;
    private final LocalDateTime createdAt;
    private final Long matchingPostIdx;

    public ChatRoomResponse(Integer chatRoomIdx,
                            Integer peerIdx,
                            String peerNickname,
                            String peerProfileImageUrl,
                            LocalDateTime createdAt,
                            Long matchingPostIdx) {
        this.chatRoomIdx = chatRoomIdx;
        this.peerIdx     = peerIdx;
        this.peerNickname = peerNickname;
        this.peerProfileImageUrl = peerProfileImageUrl;
        this.createdAt   = createdAt;
        this.matchingPostIdx = matchingPostIdx;
    }
}