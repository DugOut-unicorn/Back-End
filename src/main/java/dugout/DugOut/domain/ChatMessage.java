package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatMessage")
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "messageIdx")
    private Integer messageIdx;
    
    @Column(name = "chatRoomIdx2", nullable = false)
    private Integer chatRoomIdx2;
    
    @Column(name = "userIdx", nullable = false)
    private Integer userIdx;
    
    @Column(name = "chatRoomIdx", nullable = false)
    private Integer chatRoomIdx;
    
    @Column(name = "senderIdx", nullable = false)
    private Integer senderIdx;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @CreationTimestamp
    @Column(name = "sentAt", nullable = false, updatable = false)
    private LocalDateTime sentAt;
} 