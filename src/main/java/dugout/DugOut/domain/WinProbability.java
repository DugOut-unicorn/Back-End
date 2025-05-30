package dugout.DugOut.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "win_probabilities")
@IdClass(WinProbabilityId.class)
@Getter
@Setter
public class WinProbability {
    @Id
    @Column(name = "team1", nullable = false)
    private String team1;

    @Id
    @Column(name = "team2", nullable = false)
    private String team2;

    @Column(name = "win_probability")
    private Double winProbability;

    @Column(name = "prediction_date", nullable = false)
    private String updatedAt;
}
