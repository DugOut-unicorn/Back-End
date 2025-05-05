package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "team_record")
@Getter @Setter @NoArgsConstructor
public class TeamRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_record_idx")
    private Integer teamRecordIdx;

    @Column(name = "team_idx", nullable = false)
    private Integer teamIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_idx", insertable = false, updatable = false)
    private Team team;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "game", nullable = false)
    private Integer game;

    @Column(name = "win", nullable = false)
    private Integer win;

    @Column(name = "lose", nullable = false)
    private Integer lose;

    @Column(name = "draw", nullable = false)
    private Integer draw;

    @Column(name = "win_rate", precision = 5, scale = 3)
    private BigDecimal winRate;

    @Column(name = "game_gap", precision = 4, scale = 1)
    private BigDecimal gameGap;

    @Column(name = "recent_ten")
    private String recentTen;

    @Column(name = "streak")
    private String streak;

    @Column(name = "home_record")
    private String homeRecord;

    @Column(name = "away_record")
    private String awayRecord;
}

 