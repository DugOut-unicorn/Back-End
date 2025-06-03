package dugout.DugOut.web.controller;


import dugout.DugOut.service.TeamRankingService;
import dugout.DugOut.web.dto.response.TeamRankingsResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/final-rankings")
public class TeamRankingsContoller {
    private final TeamRankingService teamRankingService;

    public TeamRankingsContoller(TeamRankingService teamRankingService) {
        this.teamRankingService = teamRankingService;
    }

    @Operation(
            summary = "시즌 종료 후 최종 랭킹 조회"
    )
    @GetMapping
    public ResponseEntity<List<TeamRankingsResponse>> getFinalRanking() {
        List<TeamRankingsResponse> rankings = teamRankingService.getFinalRanking();
        return ResponseEntity.ok(rankings);
    }
}
