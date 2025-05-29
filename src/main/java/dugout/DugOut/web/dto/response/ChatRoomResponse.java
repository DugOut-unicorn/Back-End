package dugout.DugOut.web.dto.response;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRoomResponse {
    private final Integer chatRoomIdx;
    private final Integer peerIdx;
    private final LocalDateTime createdAt;

    public ChatRoomResponse(Integer chatRoomIdx,
                            Integer peerIdx,
                            LocalDateTime createdAt) {
        this.chatRoomIdx = chatRoomIdx;
        this.peerIdx     = peerIdx;
        this.createdAt   = createdAt;
    }
}