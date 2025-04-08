package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lineUp")
@Getter
@Setter
@NoArgsConstructor
public class LineUp {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lineupIdx")
    private Integer lineupIdx;
    
    @Column(name = "gameIdx", nullable = false)
    private Integer gameIdx;
    
    @Column(name = "stadiumIdx", nullable = false)
    private Integer stadiumIdx;
    
    @Column(name = "playerIdx", nullable = false)
    private Integer playerIdx;
    
    @Column(name = "teamIdx", nullable = false)
    private Integer teamIdx;
    
    @Column(name = "lineupOrder", nullable = false)
    private Integer lineupOrder;
    
    @Column(nullable = false, length = 10)
    private String position;
} 