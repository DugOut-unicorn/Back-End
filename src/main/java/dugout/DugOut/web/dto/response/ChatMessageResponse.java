package dugout.DugOut.web.dto.response;

import dugout.DugOut.domain.ChatMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 클라이언트에 반환할 DTO
@Getter
public class ChatMessageResponse {
    private final Integer messageIdx;
    private final Integer chatRoomIdx;
    private final Integer senderIdx;
    private final Integer receiverIdx;
    private final String content;
    private final LocalDateTime sentAt;

    public ChatMessageResponse(ChatMessage msg) {
        this.messageIdx   = msg.getMessageIdx();
        this.chatRoomIdx  = msg.getChatRoom().getChatRoomIdx();
        this.senderIdx    = msg.getSenderIdx();
        this.receiverIdx  = msg.getReceiverIdx();
        this.content      = msg.getContent();
        this.sentAt       = msg.getSentAt();
    }
}