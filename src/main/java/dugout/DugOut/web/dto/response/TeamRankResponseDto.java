package dugout.DugOut.web.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRankResponseDto {
    private Integer teamIdx;
    private Integer game;
    private Integer win;
    private Integer draw;
    private Integer lose;
    private Double winRate;
    private Double gameGap;
    private String streak;
    private String recentTen;

    public TeamRankResponseDto(Integer teamIdx, Integer game, Integer win, Integer draw, 
                             Integer lose, Double winRate, Double gameGap, 
                             String streak, String recentTen) {
        this.teamIdx = teamIdx;
        this.game = game;
        this.win = win;
        this.draw = draw;
        this.lose = lose;
        this.winRate = winRate;
        this.gameGap = gameGap;
        this.streak = streak;
        this.recentTen = recentTen;
    }
} 