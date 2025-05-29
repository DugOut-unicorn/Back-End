package dugout.DugOut.repository;

import dugout.DugOut.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    Optional<ChatRoom> findByUser1IdxAndUser2Idx(int user1Idx, int user2Idx);

    Optional<ChatRoom> findByUser2IdxAndUser1Idx(int user2Idx, int user1Idx);

    List<ChatRoom> findByUser1IdxOrUser2IdxOrderByCreatedAtDesc(int user1Idx, int user2Idx);

    /**
     * 두 사용자 간 기존 채팅방 찾기 (user1,user2 순서 상관없이)
     */
    @Query("""
      select r
      from ChatRoom r
      where (r.user1Idx = :u1 and r.user2Idx = :u2)
         or (r.user1Idx = :u2 and r.user2Idx = :u1)
    """)
    Optional<ChatRoom> findByUsers(@Param("u1") int user1,
                                   @Param("u2") int user2);
} 