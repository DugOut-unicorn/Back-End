package dugout.DugOut.web.controller;

import dugout.DugOut.domain.ChatMessage;
import dugout.DugOut.domain.ChatRoom;
import dugout.DugOut.domain.User;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.service.ChatService;
import dugout.DugOut.service.JwtService;
import dugout.DugOut.web.dto.ChatMessageDto;
import dugout.DugOut.web.dto.request.CreateChatRoomRequest;
import dugout.DugOut.web.dto.response.ChatMessageResponse;
import dugout.DugOut.web.dto.response.ChatRoomResponse;
import dugout.DugOut.web.dto.response.CreateChatRoomResponse;
import io.jsonwebtoken.Jwt;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final SimpMessagingTemplate template;
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public ChatController(SimpMessagingTemplate template, ChatService chatService, UserRepository userRepository, JwtService jwtService) {
        this.template = template;
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    private User getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtService.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

//    @MessageMapping("/chat.send")
//    public void sendMessage(ChatTestDto msg) {
//        // 로깅이 필요하면 msg.getFrom()을 쓰세요
//        System.out.println("▶ sendMessage: from=" + msg.getFrom() + " to=" + msg.getTo());
//        template.convertAndSendToUser(
//                msg.getTo(),
//                "/queue/messages",
//                msg
//        );
//        System.out.println("▶ convertAndSendToUser for: " + msg.getTo());
//    }


    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageDto dto) {
        ChatMessage saved = chatService.saveMessage(
                /*roomId=*/0, // 내부에서 getOrCreateRoom 처리
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

    // 과거 메시지 조회 REST API
    @GetMapping("/api/chat/history")
    @ResponseBody
    public List<ChatMessageResponse> history(
            @RequestParam int userA,
            @RequestParam int userB) {

        ChatRoom room = chatService.getOrCreateRoom(userA, userB);
        return chatService.getHistory(room.getChatRoomIdx())
                .stream()
                .map(ChatMessageResponse::new)
                .toList();
    }

    /**
     * 내가 참여한 1:1 채팅방 목록 조회
     * JWT로 인증된 Principal.getName()을 userId로 사용한다고 가정
     */
    @GetMapping("/rooms")
    public List<ChatRoomResponse> getMyChatRooms(HttpServletRequest request) {
        User user = getCurrentUser(request);
        Integer userId = user.getUserIdx();
        return chatService.getUserChatRooms(userId);
    }


    @Operation(summary = "매칭 글 채팅방 생성 또는 조회",
            description = "matchingPostId 로 글 작성자와 1:1 채팅방을 생성하거나, 이미 방이 있으면 그 방을 반환합니다.")
    @PostMapping
    public ResponseEntity<CreateChatRoomResponse> createOrGetRoom(
            HttpServletRequest request,
            @Valid @RequestBody CreateChatRoomRequest req
    ) {

        User user = getCurrentUser(request);
        Integer currentUserId = user.getUserIdx();

        ChatService.ChatRoomInfo info = chatService.getOrCreateRoomForPost(
                Long.valueOf(currentUserId),
                req.matchingPostId()
        );

        // 3) 응답 DTO 포장 후 반환
        CreateChatRoomResponse resp = new CreateChatRoomResponse(
                info.chatRoomId(), info.otherUserId()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resp);
    }
}