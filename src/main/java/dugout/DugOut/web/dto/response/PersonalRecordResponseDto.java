package dugout.DugOut.web.dto.response;

import dugout.DugOut.domain.DefenseStat;
import dugout.DugOut.domain.HitterStat;
import dugout.DugOut.domain.PitcherStat;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class PersonalRecordResponseDto {
    // 선수 기본 정보
    private Integer playerIdx;
    private String playerName;
    private Integer teamIdx;
    private Integer backNumber;
    private String position;
    private String birthday;
    private String heightWeight;
    private String career;
    private String playerImageUrl;

    // 포지션별 기록
    private PitcherStat pitcherStat;
    private HitterStat hitterStat;
    private DefenseStat defenseStat;

    // 기록 존재 여부
    private boolean hasRecord;

    public PersonalRecordResponseDto(String playerName, Integer teamIdx, Integer backNumber, 
                                   String position, String birthday, String heightWeight, 
                                   String career, String playerImageUrl) {
        this.playerName = playerName;
        this.teamIdx = teamIdx;
        this.backNumber = backNumber;
        this.position = position;
        this.birthday = birthday;
        this.heightWeight = heightWeight;
        this.career = career;
        this.playerImageUrl = playerImageUrl;
        this.hasRecord = false;
    }
} 