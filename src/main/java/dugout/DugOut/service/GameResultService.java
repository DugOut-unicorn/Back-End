package dugout.DugOut.service;

import dugout.DugOut.repository.GameResultRepository;
import dugout.DugOut.web.dto.response.GameResultResponse;
import dugout.DugOut.web.dto.response.RecentResultDto;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class GameResultService {
    private final GameResultRepository repo;

    public GameResultService(GameResultRepository repo) {
        this.repo = repo;
    }

    public GameResultResponse getRecent(LocalDate baseDate, int limit) {
        // 1) 기준일 전날~7일 전 가장 최근 날짜 조회
        LocalDate upper = baseDate.minusDays(1);
        LocalDate lower = baseDate.minusDays(7);
        Date dt = repo.findMostRecentMatchDate(lower, upper);
        if (dt == null) {
            return new GameResultResponse(baseDate, null, List.of());
        }
        LocalDate matchDate = dt.toLocalDate();

        // 2) 해당 날짜의 경기 결과 최대 limit개 조회
        List<RecentResultDto> items =
                repo.findRecentResultDtosByMatchDate(matchDate, limit);

        // 3) 래핑해서 반환
        return new GameResultResponse(baseDate, matchDate, items);
    }
}
