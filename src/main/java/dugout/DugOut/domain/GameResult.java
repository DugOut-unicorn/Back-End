package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "game_result")
public class GameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    // 스칼라 타입으로만 FK 관리
    @Column(name = "game_idx", nullable = false)
    private Integer gameIdx;

    @Column(name="home_score")
    private Integer homeScore;

    @Column(name="away_score")
    private Integer awayScore;

    @Column(name="recorded_at")
    private LocalDateTime recordedAt;

}
