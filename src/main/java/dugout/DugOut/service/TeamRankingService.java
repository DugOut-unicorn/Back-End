package dugout.DugOut.service;

import dugout.DugOut.domain.TeamRankings;
import dugout.DugOut.domain.TeamRecord;
import dugout.DugOut.repository.TeamRankingRepository;
import dugout.DugOut.repository.TeamRecordRepository;
import dugout.DugOut.web.dto.response.TeamRankingResponse;
import dugout.DugOut.web.dto.response.TeamRankingsResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeamRankingService {

    private final TeamRecordRepository teamRecordRepo;
    private final TeamRankingRepository teamRankingRepository;

    public TeamRankingService(TeamRecordRepository teamRecordRepo, TeamRankingRepository teamRankingRepository) {
        this.teamRecordRepo = teamRecordRepo;
        this.teamRankingRepository = teamRankingRepository;
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

    private static final Map<String, Integer> TEAM_NAME_TO_IDX = Map.of(
            "LG",    1,
            "SSG",   2,
            "삼성",   3,
            "KT",    4,
            "롯데",   5,
            "NC",    6,
            "두산",   7,
            "키움",   8,
            "KIA",   9,
            "한화",  10
    );

    public List<TeamRankingsResponse> getFinalRanking() {
        List<TeamRankings> entities = teamRankingRepository.findByIdBetweenOrderByIdAsc(1, 10);

        // 2) "팀 레코드" 테이블에서 가장 최신 날짜 구하기
        LocalDate latestDate = teamRecordRepo.findLatestDate();
        if (latestDate == null) {
            // 레코드 자체가 없다면 모든 currentRank를 null로 둔 채로 DTO만 반환
            return entities.stream()
                    .map(e -> TeamRankingsResponse.of(e, null))
                    .collect(Collectors.toList());
        }

        // 3) 최신 날짜의 모든 팀 기록을 winRate 내림차순으로 조회 → 순위를 매긴다
        List<TeamRecord> latestRecords = teamRecordRepo.findAllByDateOrderByWinRateDesc(latestDate);

        // 4) 팀 idx → 실제(current) 순위(Map) 생성
        Map<Integer, Integer> idxToActualRank = new HashMap<>();
        int rankCounter = 1;
        for (TeamRecord rec : latestRecords) {
            // TeamRecord에서 teamIdx를 꺼내는 부분은 실제 엔티티 구조에 맞게 수정하세요.
            // 예) rec.getTeam().getId() 또는 rec.getTeamIdx() 등
            Integer teamIdx = rec.getTeam().getTeamIdx();
            idxToActualRank.put(teamIdx, rankCounter++);
        }

        // 5) 최종적으로 "예측 순위" 엔티티 리스트를 순회하면서 DTO 생성
        return entities.stream()
                .map(entity -> {
                    // 5-1) 팀 이름 → 팀 idx
                    Integer teamIdx = TEAM_NAME_TO_IDX.get(entity.getTeamName());
                    // 5-2) idxToActualRank에서 실제 순위를 꺼냄
                    Integer actualRank = (teamIdx != null) ? idxToActualRank.get(teamIdx) : null;
                    return TeamRankingsResponse.of(entity, actualRank);
                })
                .collect(Collectors.toList());
    }
}

