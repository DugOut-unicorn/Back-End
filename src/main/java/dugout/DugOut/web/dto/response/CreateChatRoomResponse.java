package dugout.DugOut.web.dto.response;

import lombok.Getter;

public record CreateChatRoomResponse(
        Long chatRoomId,
        Long withUserId
) { }
