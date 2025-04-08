package dugout.DugOut.domain;

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
    
    @Column(name = "home_team_idx", nullable = false)
    private Integer homeTeamIdx;
    
    @Column(name = "stadium_idx", nullable = false)
    private Integer stadiumIdx;
    
    @Column(name = "away_team_idx", nullable = false)
    private Integer awayTeamIdx;
    
    @Column(name = "start_time", nullable = false)
    private Integer startTime;
    
    @Column(nullable = false)
    private LocalDateTime date;
} 