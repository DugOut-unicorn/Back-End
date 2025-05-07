package dugout.DugOut.service;

import dugout.DugOut.domain.TeamRecord;
import dugout.DugOut.repository.TeamRecordRepository;
import dugout.DugOut.web.dto.response.TeamRankingResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamRankingService {

    private final TeamRecordRepository teamRecordRepo;

    public TeamRankingService(TeamRecordRepository teamRecordRepo) {
        this.teamRecordRepo = teamRecordRepo;
    }

    /**
     * 가장 최근 날짜의 팀 랭킹 요약 (팀명, 경기, 승, 무, 패) 반환
     */
    public List<TeamRankingResponse> getLatestRanking() {
        LocalDate latestDate = teamRecordRepo.findLatestDate();
        if (latestDate == null) {
            return List.of();  // 데이터가 하나도 없으면 빈 리스트
        }
        List<TeamRecord> records = teamRecordRepo.findAllByDateOrderByWinRateDesc(latestDate);
        return records.stream()
                .map(tr -> new TeamRankingResponse(
                        tr.getTeam().getTeamName(),
                        tr.getGame(),
                        tr.getWin(),
                        tr.getDraw(),
                        tr.getLose()
                ))
                .collect(Collectors.toList());
    }
}

