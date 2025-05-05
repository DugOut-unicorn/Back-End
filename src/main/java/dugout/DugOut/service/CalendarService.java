package dugout.DugOut.service;

import dugout.DugOut.domain.Game;
import dugout.DugOut.repository.GameRepository;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.web.dto.response.CalendarDateResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarService {
    private final UserRepository userRepo;
    private final GameRepository gameRepo;

    public CalendarService(UserRepository userRepo, GameRepository gameRepo) {
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
    }

    /**
     * userIdx가 응원하는 팀의, year-month 월별 경기일(일자만) 배열을
     * {year, month, days: [...]} 형태로 반환
     */
    public CalendarDateResponse getCalendarDays(Integer userIdx, YearMonth ym) {
        int year  = ym.getYear();
        int month = ym.getMonthValue();

        // 1) 사용자 응원팀 ID
        Integer teamId = userRepo.findCheeringTeamIdByUserIdx(userIdx);

        // 2) 해당 월의 시작 시각과 다음 달 시작 시각
        LocalDateTime startOfMonth     = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);

        // 3) 그 기간의 모든 경기 불러오기
        List<Game> games = gameRepo.findGamesByPeriod(startOfMonth, startOfNextMonth);

        // 4) 응원팀이 나오는 경기만 필터 → 날짜(dayOfMonth)만 뽑아서 중복 제거·정렬
        List<Integer> days = games.stream()
                .filter(g -> g.getHomeTeamIdx().equals(teamId)
                        || g.getAwayTeamIdx().equals(teamId))
                .map(g -> g.getDate().toLocalDate().getDayOfMonth())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // 5) 최종 반환
        return new CalendarDateResponse(year, month, days);
    }
}
