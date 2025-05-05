package dugout.DugOut.domain;

import dugout.DugOut.domain.enums.GameStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "game")
@Getter
@Setter
@NoArgsConstructor
public class Game {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_idx")
    private Integer gameIdx;

    // Java 필드는 camelCase
    @Column(name = "home_team_idx", nullable = false)
    private Integer homeTeamIdx;

    @Column(name = "away_team_idx", nullable = false)
    private Integer awayTeamIdx;

    @Column(name = "stadium_idx", nullable = false)
    private Integer stadiumIdx;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status;
} 