package dugout.DugOut.web.dto.response;

import dugout.DugOut.domain.TeamRankings;
import lombok.Getter;

@Getter
public class TeamRankingsResponse {
    private final Integer id;
    private final String teamName;
    private final Integer rank;
    private final Integer currentRank;

    public TeamRankingsResponse(Integer id, String teamName, Integer rank, Integer currentRank) {
        this.id = id;
        this.teamName = teamName;
        this.rank = rank;
        this.currentRank = currentRank;
    }

    /** 엔티티 → DTO 변환 헬퍼 메서드 */
    public static TeamRankingsResponse of(TeamRankings entity, Integer currentRank) {
        return new TeamRankingsResponse(
                entity.getId(),
                entity.getTeamName(),
                entity.getRank(),
                currentRank
        );
    }
}
