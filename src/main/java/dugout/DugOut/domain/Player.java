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
    @Column(name = "player_idx")
    private Integer playerIdx;
    
    @Column(name = "team_idx", nullable = false)
    private Integer teamIdx;
    
    @Column(name = "player_name", nullable = false, length = 20)
    private String playerName;
    
    @Column(name = "player_image_url")
    private String playerImageUrl;

    @Column(name = "back_number")
    private Integer backNumber;

    @Column(name = "position", length = 10)
    private String position;

    @Column(name = "sub_position", length = 10)
    private String sub_position;

    @Column(name = "birthday")
    private String birthday;

    @Column(name = "height_weight")
    private String heightWeight;

    @Column(name = "career")
    private String career;
} 