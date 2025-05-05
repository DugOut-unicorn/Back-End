package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "pitcherStat")
@Getter
@Setter
@NoArgsConstructor
public class PitcherStat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pitStatIdx")
    private Integer pitStatIdx;
    
    @Column(name = "playerIdx", nullable = false)
    private Integer playerIdx;
    
    @Column(name = "teamIdx", nullable = false)
    private Integer teamIdx;
    
    private Integer g;
    
    private Integer w;
    
    private Integer l;
    
    private Integer sv;
    
    private Integer hld;
    
    @Column(columnDefinition = "DECIMAL(4,1)")
    private BigDecimal ip;
    
    private Integer h;
    
    private Integer hr;
    
    private Integer bb;
    
    private Integer hbp;
    
    private Integer so;
    
    private Integer r;
    
    private Integer er;
    
    private Integer cg;
    
    private Integer sho;
    
    private Integer qs;
    
    private Integer bsv;
    
    private Integer tbf;
    
    private Integer np;
    
    @Column(name = "2b")
    private Integer double2b;
    
    @Column(name = "3b")
    private Integer triple3b;
    
    private Integer sac;
    
    private Integer sf;
    
    private Integer ibb;
    
    private Integer wp;
    
    private Integer bk;
    
    private Integer gs;
    
    private Integer wgs;
    
    private Integer wgr;
    
    private Integer ggf;
    
    private Integer ssvo;
    
    private Integer ts;
    
    private Integer gdp;
    
    private Integer go;
    
    private Integer ao;
} 
 