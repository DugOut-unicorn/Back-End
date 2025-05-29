package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_idx", updatable = false, nullable = false)
    private Integer chatRoomIdx;

    @Column(name = "matching_post_idx", nullable = false)
    private Long matchingPostIdx;

    @Column(name = "user1idx", nullable = false)
    private Integer user1Idx;

    @Column(name = "user2idx", nullable = false)
    private Integer user2Idx;

    @Column(name = "user1last_read_msg_idx")
    private Integer user1LastReadMsgIdx;

    @Column(name = "user2last_read_msg_idx")
    private Integer user2LastReadMsgIdx;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
