package dugout.DugOut.web.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatHistoryResponse {
    private final int myId;
    private final List<ChatMessageResponse> messages;

    public ChatHistoryResponse(int myId, List<ChatMessageResponse> messages) {
        this.myId     = myId;
        this.messages = messages;
    }
}