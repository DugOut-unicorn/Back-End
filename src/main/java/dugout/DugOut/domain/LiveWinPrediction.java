package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "live_win_prediction")
@Getter
@Setter
@NoArgsConstructor
public class LiveWinPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "game_id", nullable = false)
    private String gameId;

    @Column(name="inning", nullable = false)
    private Integer inning;

    @Column(name="win_probability")
    private Double winProbability;

    @Column(name="home_accum_score")
    private Integer homeAccumScore;

    @Column(name="away_accum_score")
    private Integer awayAccumScore;


}
