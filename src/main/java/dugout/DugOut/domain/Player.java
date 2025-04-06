package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "player")
@Getter
@Setter
@NoArgsConstructor
public class Player {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_idx", columnDefinition = "INT AUTO_INCREMENT")
    private Integer playerIdx;
    
    @Column(name = "team_idx", nullable = false)
    private Integer teamIdx;
    
    @Column(name = "player_name", nullable = false, length = 20)
    private String playerName;
} 