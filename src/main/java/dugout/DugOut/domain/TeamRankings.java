package dugout.DugOut.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "team_rankings")
@Getter
@Setter
@NoArgsConstructor
public class TeamRankings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "rank", nullable = false)
    private Integer rank;

    @Column(name = "team_name", length = 20)
    private String teamName;
}