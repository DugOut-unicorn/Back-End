package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "matchingPost")
@Getter
@Setter
@NoArgsConstructor
public class MatchingPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matchingPostIdx")
    private Integer matchingPostIdx;
    
    @Column(name = "userIdx", nullable = false)
    private Integer userIdx;
    
    @Column(name = "gameIdx", nullable = false)
    private Integer gameIdx;
    
    @Column(name = "stadiumIdx2", nullable = false)
    private Integer stadiumIdx2;
    
    @Column(name = "teamIdx")
    private Integer teamIdx;
    
    @Column(columnDefinition = "TEXT")
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String context;
    
    @Column(name = "haveTicket")
    private Integer haveTicket;
    
    @CreationTimestamp
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private Integer status = 0;
    
    @Column(name = "isMatched", nullable = false)
    private Integer isMatched = 0;
} 