package dugout.DugOut.web.controller;

import dugout.DugOut.domain.ChatMessage;
import dugout.DugOut.repository.UserRepository;
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
    private final UserRepository userRepository;

    public ChatMessageController(ChatService chatService, SimpMessagingTemplate template, UserRepository userRepository) {
        this.chatService = chatService;
        this.template = template;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageDto dto) {
        System.out.println("▶ sendMessage 호출, roomIdx=" + dto.getRoomIdx() +
                ", senderIdx=" + dto.getSenderIdx() +
                ", receiverIdx=" + dto.getReceiverIdx() +
                ", content=" + dto.getContent());

        ChatMessage saved = chatService.saveMessage(
                dto.getRoomIdx(),
                dto.getSenderIdx(),
                dto.getReceiverIdx(),
                dto.getContent()
        );
        ChatMessageResponse resp = new ChatMessageResponse(saved);

        // 2) 받는 사람 email 조회
        String receiverEmail = userRepository.findById(dto.getReceiverIdx())
                .orElseThrow().getEmail();
        // 3) 보내는 사람 email 조회 (에코)
        String senderEmail   = userRepository.findById(dto.getSenderIdx())
                .orElseThrow().getEmail();

        // 4) STOMP 브로커에 email 기반으로 발행
        template.convertAndSendToUser(
                receiverEmail,   // Principal.name 에 매핑되는 email
                "/queue/messages",
                resp
        );
    }
}
