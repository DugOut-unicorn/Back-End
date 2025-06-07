package dugout.DugOut.repository;

import dugout.DugOut.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    List<ChatMessage> findByChatRoom_ChatRoomIdxOrderBySentAtAsc(Integer chatRoomIdx);
} 