package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "teamRecord")
@Getter
@Setter
@NoArgsConstructor
public class TeamRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_record_idx", columnDefinition = "INT AUTO_INCREMENT")
    private Integer teamRecordIdx;
    
    @Column(name = "team_idx", nullable = false)
    private Integer teamIdx;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private Integer game;
    
    @Column(nullable = false)
    private Integer win;
    
    @Column(nullable = false)
    private Integer lose;
    
    @Column(nullable = false)
    private Integer home;
    
    @Column(nullable = false)
    private Integer away;
    
    @Column(nullable = false)
    private Integer draw;
} 