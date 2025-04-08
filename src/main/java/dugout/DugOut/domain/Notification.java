package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_idx")
    private Integer notificationIdx;
    
    @Column(name = "user_idx", nullable = false)
    private Integer userIdx;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;
    
    @Column(nullable = false, length = 255)
    private String message;
    
    @Column(name = "link_url", length = 255)
    private String linkUrl;
    
    @Column(name = "is_read", nullable = false)
    private Integer isRead = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum NotificationType {
        REVIEW_REQUEST, NEW_MESSAGE
    }
} 