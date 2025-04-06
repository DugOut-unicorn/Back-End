package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "hitterStat")
@Getter
@Setter
@NoArgsConstructor
public class HitterStat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hitStatIdx")
    private Integer hitStatIdx;
    
    @Column(name = "playerIdx", nullable = false)
    private Integer playerIdx;
    
    @Column(name = "teamIdx", nullable = false)
    private Integer teamIdx;
    
    private Integer g;
    
    private Integer pa;
    
    private Integer ab;
    
    private Integer r;
    
    private Integer h;
    
    @Column(name = "2b")
    private Integer double2b;
    
    @Column(name = "3b")
    private Integer triple3b;
    
    private Integer hr;
    
    private Integer rbi;
    
    private Integer sac;
    
    private Integer sf;
    
    private Integer bb;
    
    private Integer ibb;
    
    private Integer hbp;
    
    private Integer so;
    
    private Integer gdp;
    
    private Integer mh;
    
    @Column(columnDefinition = "DECIMAL(5,3)")
    private BigDecimal risp;
    
    @Column(name = "ph-ba", columnDefinition = "DECIMAL(5,3)")
    private BigDecimal phBa;
    
    private Integer go;
    
    private Integer ao;
    
    @Column(name = "gw_rbi")
    private Integer gwRbi;
    
    @Column(columnDefinition = "DECIMAL(5,3)")
    private BigDecimal isop;
    
    @Column(columnDefinition = "DECIMAL(5,3)")
    private BigDecimal xr;
} 