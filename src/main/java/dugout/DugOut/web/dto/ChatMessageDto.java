package dugout.DugOut.web.dto;

import lombok.Getter;
import lombok.Setter;

// 클라이언트에서 보낼 DTO
@Getter
@Setter
public class ChatMessageDto {
    private Integer roomIdx;
    private Integer senderIdx;
    private Integer receiverIdx;
    private String content;
    // getters/setters
}
