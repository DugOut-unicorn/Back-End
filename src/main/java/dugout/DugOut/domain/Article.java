package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "article")
@Getter
@Setter
@NoArgsConstructor
public class Article {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_idx")
    private Integer articleIdx;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;
    
    @Column(name = "thumbnail_url", nullable = false, length = 255)
    private String thumbnailUrl;
    
    @Column(name = "link_url", nullable = false, length = 255)
    private String linkUrl;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
} 