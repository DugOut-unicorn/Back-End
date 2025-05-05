package dugout.DugOut.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_result")
public class GameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @OneToOne
    @JoinColumn(name = "game_idx")
    private Game game;

    @Column(name="home_score")
    private Integer homeScore;

    @Column(name="away_score")
    private Integer awayScore;

    @Column(name="recorded_at")
    private LocalDateTime recordedAt;

}
