package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "matching_post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor    // 모든 필드를 파라미터로 받는 생성자
@Builder
public class MatchingPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_post_idx")
    private Long matchingPostIdx;

    @Column(name = "context", columnDefinition = "TEXT")
    private String context;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "game_idx", nullable = false)
    private Integer gameIdx;

    @Column(name = "preferred_match_date")
    private LocalDate preferredMatchDate;

    @Column(name = "have_ticket")
    private Boolean haveTicket;

    @Column(name = "is_matched", nullable = false)
    @Builder.Default
    private Integer isMatched = 0;

    @Column(name = "stadium_idx", nullable = false)
    private Integer stadiumIdx;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 0;

    @Column(name = "cheering_team_idx")
    private Integer cheeringTeamIdx;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    // JPA 양방향이 아니라 단방향으로 User 를 읽기만
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", insertable = false, updatable = false)
    private User user;

    @Column(name = "user_idx", nullable = false)
    private Integer userIdx;
}
