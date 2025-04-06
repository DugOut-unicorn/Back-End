package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stadium")
@Getter
@Setter
@NoArgsConstructor
public class Stadium {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stadium_idx", columnDefinition = "INT AUTO_INCREMENT")
    private Integer stadiumIdx;
    
    @Column(name = "team_idx", nullable = false)
    private Integer teamIdx;
    
    @Column(name = "stadium_name", length = 20)
    private String stadiumName;
} 