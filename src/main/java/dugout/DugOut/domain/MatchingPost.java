package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "matching_post")
@Getter
@Setter
@NoArgsConstructor
public class MatchingPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_post_idx")
    private Integer matchingPostIdx;

    @Column(name = "user_idx", nullable = false)
    private Integer userIdx;

    // JPA 양방향이 아니라 단방향으로 User 를 읽기만
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", insertable = false, updatable = false)
    private User user;

    @Column(name = "game_idx", nullable = false)
    private Integer gameIdx;

    @Column(name = "stadium_idx", nullable = false)
    private Integer stadiumIdx;

    @Column(name = "team_idx")
    private Integer teamIdx;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "context", columnDefinition = "TEXT")
    private String context;

    @Column(name = "have_ticket")
    private Integer haveTicket;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false)
    private Integer status = 0;

    @Column(name = "is_matched", nullable = false)
    private Integer isMatched = 0;
}
