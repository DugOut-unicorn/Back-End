package dugout.DugOut.web.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateChatRoomRequest(
        @NotNull Long matchingPostId
) { }
