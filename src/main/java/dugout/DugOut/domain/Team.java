package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "team")
@Getter
@Setter
@NoArgsConstructor
public class Team {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_idx", columnDefinition = "INT AUTO_INCREMENT")
    private Integer teamIdx;
    
    @Column(name = "team_name", length = 20)
    private String teamName;
} 