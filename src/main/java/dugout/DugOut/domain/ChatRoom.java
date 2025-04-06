package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatRoom")
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatRoomIdx")
    private Integer chatRoomIdx;
    
    @Column(name = "user1Idx", nullable = false)
    private Integer user1Idx;
    
    @Column(name = "user2Idx", nullable = false)
    private Integer user2Idx;
    
    @Column(name = "user1LastReadMsgIdx")
    private Integer user1LastReadMsgIdx;
    
    @Column(name = "user2LastReadMsgIdx")
    private Integer user2LastReadMsgIdx;
    
    @CreationTimestamp
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;
} 