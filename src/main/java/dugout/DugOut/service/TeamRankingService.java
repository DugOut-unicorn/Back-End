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
     * 요청 날짜(targetDate)에 대한 팀 랭킹 요약 (팀명, 경기, 승, 무, 패) 반환
     */
    public List<TeamRankingResponse> getRankingByDate(LocalDate targetDate) {
        List<TeamRecord> records = teamRecordRepo.findAllByDateOrderByWinRateDesc(targetDate);

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
