package dugout.DugOut.web.controller;

import dugout.DugOut.domain.ChatMessage;
import dugout.DugOut.service.ChatService;
import dugout.DugOut.web.dto.ChatMessageDto;
import dugout.DugOut.web.dto.response.ChatMessageResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatMessageController {

    private final ChatService chatService;
    private final SimpMessagingTemplate template;

    public ChatMessageController(ChatService chatService, SimpMessagingTemplate template) {
        this.chatService = chatService;
        this.template = template;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageDto dto) {
        ChatMessage saved = chatService.saveMessage(
                dto.getRoomIdx(),
                dto.getSenderIdx(),
                dto.getReceiverIdx(),
                dto.getContent()
        );
        ChatMessageResponse resp = new ChatMessageResponse(saved);
        // 1:1 대화 상대에게 보냄
        template.convertAndSendToUser(
                dto.getReceiverIdx().toString(),
                "/queue/messages",
                resp
        );
        // 본인에게도 에코(선택)
        template.convertAndSendToUser(
                dto.getSenderIdx().toString(),
                "/queue/messages",
                resp
        );
    }
}
