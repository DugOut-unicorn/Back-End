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
    @Column(name = "gameIdx")
    private Integer gameIdx;
    
    @Column(name = "homeTeamIdx", nullable = false)
    private Integer homeTeamIdx;
    
    @Column(name = "stadiumIdx", nullable = false)
    private Integer stadiumIdx;
    
    @Column(name = "awayTeamIdx", nullable = false)
    private Integer awayTeamIdx;
    
    @Column(name = "startTime", nullable = false)
    private Integer startTime;
    
    @Column(nullable = false)
    private LocalDateTime date;
} 