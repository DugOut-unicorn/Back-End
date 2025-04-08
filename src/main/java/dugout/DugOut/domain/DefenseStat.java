package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "defenseStat")
@Getter
@Setter
@NoArgsConstructor
public class DefenseStat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "def_stat_idx", columnDefinition = "INT AUTO_INCREMENT")
    private Integer defStatIdx;
    
    @Column(name = "player_idx", nullable = false)
    private Integer playerIdx;
    
    @Column(name = "team_idx", nullable = false)
    private Integer teamIdx;
    
    @Column(length = 20)
    private String pos;
    
    private Integer g;
    
    private Integer gs;
    
    private Float ip;
    
    private Integer e;
    
    private Integer pko;
    
    private Integer po;
    
    private Integer a;
    
    private Integer dp;
    
    private Integer pb;
    
    private Integer sb;
    
    private Integer cs;
} 