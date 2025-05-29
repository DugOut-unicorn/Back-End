package dugout.DugOut.service;

import dugout.DugOut.domain.ChatMessage;
import dugout.DugOut.domain.ChatRoom;
import dugout.DugOut.domain.User;
import dugout.DugOut.repository.ChatMessageRepository;
import dugout.DugOut.repository.ChatRoomRepository;
import dugout.DugOut.repository.MatchingPostRepository;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.web.dto.response.ChatRoomResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;
    private final MatchingPostRepository postRepo;
    private final UserRepository userRepo;

    public ChatService(ChatRoomRepository roomRepo, ChatMessageRepository msgRepo, MatchingPostRepository postRepo, UserRepository userRepo) {
        this.roomRepo = roomRepo;
        this.msgRepo = msgRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    /** 1:1 채팅방 조회 혹은 생성 */
    public ChatRoom getOrCreateRoom(int userA, int userB) {
        return roomRepo.findByUser1IdxAndUser2Idx(userA, userB)
                .or(() -> roomRepo.findByUser2IdxAndUser1Idx(userA, userB))
                .orElseGet(() -> {
                    ChatRoom room = new ChatRoom();
                    room.setCreatedAt(LocalDateTime.now());
                    room.setUser1Idx(userA);
                    room.setUser2Idx(userB);
                    return roomRepo.save(room);
                });
    }

    /** 메시지 저장 및 반환 */
    public ChatMessage saveMessage(int roomId, int senderIdx, int receiverIdx, String content) {
        ChatRoom room = roomRepo.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));
        ChatMessage msg = new ChatMessage();
        msg.setChatRoom(room);
        msg.setSenderIdx(senderIdx);
        msg.setReceiverIdx(receiverIdx);
        msg.setContent(content);
        return msgRepo.save(msg);
    }

    public List<ChatMessage> getHistory(int roomId) {
        return msgRepo.findByChatRoom_ChatRoomIdxOrderBySentAtAsc(roomId);
    }

    /**
     * 로그인한 userId가 속한 모든 채팅방 목록을 반환합니다.
     */
    public List<ChatRoomResponse> getUserChatRooms(int userId) {
        List<ChatRoom> rooms =
                roomRepo.findByUser1IdxOrUser2IdxOrderByCreatedAtDesc(userId, userId);

        return rooms.stream()
                .map(room -> {
                    // 상대편 ID 계산
                    int peerId = room.getUser1Idx().equals(userId)
                            ? room.getUser2Idx()
                            : room.getUser1Idx();

                    User peer = userRepo.findById(peerId)
                            .orElseThrow(() -> new EntityNotFoundException("Peer user not found"));

                    return new ChatRoomResponse(
                            room.getChatRoomIdx(),
                            peerId,
                            peer.getNickname(),
                            peer.getProfileImageUrl(),
                            room.getCreatedAt(),
                            room.getMatchingPostIdx()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatRoomInfo getOrCreateRoomForPost(Long currentUserId, Long matchingPostId) {
        // 1) 글 작성자 조회
        int authorId = postRepo.findById(matchingPostId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("MatchingPost not found"))
                .getUserIdx();

        if (authorId == currentUserId.intValue()) {
            throw new IllegalArgumentException("자기 자신과는 채팅방을 만들 수 없습니다.");
        }

        // 2) 기존 방 검색
        Optional<ChatRoom> existing = roomRepo
                .findByUser1IdxAndUser2Idx(currentUserId.intValue(), authorId)
                .or(() -> roomRepo.findByUser2IdxAndUser1Idx(currentUserId.intValue(), authorId));

        ChatRoom room = existing.orElseGet(() -> {
            // 3) 새 방 생성
            ChatRoom r = new ChatRoom();
            r.setUser1Idx(currentUserId.intValue());
            r.setUser2Idx(authorId);
            r.setMatchingPostIdx(matchingPostId);
            return roomRepo.save(r);
        });

        // 4) 상대방 ID 계산
        Long otherId = room.getUser1Idx().equals(currentUserId.intValue())
                ? room.getUser2Idx().longValue()
                : room.getUser1Idx().longValue();

        return new ChatRoomInfo(room.getChatRoomIdx().longValue(), otherId);
    }


    public record ChatRoomInfo(Long chatRoomId, Long otherUserId) {}
}


