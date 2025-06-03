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
        // 1) 기준일 전날 ~ 7일 전 가장 최근 날짜 조회
        LocalDate upper = baseDate.minusDays(1);
        LocalDate lower = baseDate.minusDays(7);
        Date dt = repo.findMostRecentMatchDate(lower, upper);

        if (dt == null) {
            // 지난 7일 사이에 아예 예전 경기 결과가 없으면 빈 리스트
            return new GameResultResponse(baseDate, null, List.of());
        }

        LocalDate matchDate = dt.toLocalDate();
        // 2) 해당 matchDate의 상위 limit개 결과 조회
        List<RecentResultDto> items = repo.findRecentResultDtosByMatchDate(matchDate, limit);
        // 3) 감싸서 반환 (baseDate: today, matchDate: 조회된 그 날)
        return new GameResultResponse(baseDate, matchDate, items);
    }

    /**
     * (B) date 파라미터가 명시적으로 주어진 경우:
     *     해당 날짜(matchDate)에 경기 결과가 있으면 최대 limit개를 반환,
     *     없으면 빈 리스트 반환
     */
    public List<RecentResultDto> findByExactDate(LocalDate matchDate, int limit) {
        return repo.findRecentResultDtosByMatchDate(matchDate, limit);
    }
}
