package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_idx")
    private Integer messageIdx;

    @Column(name = "receiver_idx", nullable = false)
    private Integer receiverIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_idx", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "sender_idx", nullable = false)
    private Integer senderIdx;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;
}