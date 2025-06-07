package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "defense_stat")
@Getter
@Setter
public class DefenseStat {
    @Id
    @Column(name = "player_idx")
    private Integer playerIdx;
    
    @Column(name = "team_idx")
    private Integer teamIdx;
    
    @Column(name = "g")
    private Integer g;  // 경기 수

    @Column(name = "gs")
    private Integer gs;  // 선발 출전 수

    @Column(name = "inn")
    private Double inn;  // 이닝 수

    @Column(name = "tc")
    private Integer tc;  // 시도 수

    @Column(name = "po")
    private Integer po;  // 아웃카운트 수

    @Column(name = "a")
    private Integer a;  // 어시스트 수

    @Column(name = "e")
    private Integer e;  // 실책 수

    @Column(name = "dp")
    private Integer dp;  // 더블플레이 수

    @Column(name = "sb")
    private Integer sb;  // 도루 허용 수

    @Column(name = "cs")
    private Integer cs;  // 도루 저지 수

    @Column(name = "pb")
    private Integer pb;  // 패스트볼 수

    @Column(name = "wp")
    private Integer wp;  // 와일드피치 수

    @Column(name = "fpct")
    private Double fpct;  // 수비율
} 