package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "runningStat")
@Getter
@Setter
@NoArgsConstructor
public class RunningStat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "runStatIdx")
    private Integer runStatIdx;
    
    @Column(name = "playerIdx", nullable = false)
    private Integer playerIdx;
    
    @Column(name = "teamIdx", nullable = false)
    private Integer teamIdx;
    
    private Integer g;
    
    private Integer sba;
    
    private Float sb;
    
    private Integer cs;
    
    private Integer oob;
    
    private Integer pko;
} 
 