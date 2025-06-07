package dugout.DugOut.service;

import dugout.DugOut.domain.enums.TeamCodeMapping;
import dugout.DugOut.repository.LiveWinPredictionsRepository;
import dugout.DugOut.web.dto.LiveWinPredictionDto;
import dugout.DugOut.web.dto.response.LiveWinPredictionResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class LiveWinPredictionService {

    private final LiveWinPredictionsRepository repository;

    public LiveWinPredictionService(LiveWinPredictionsRepository repository) {
        this.repository = repository;
    }

    /**
     * 1) 가장 최근 날짜 조회
     * 2) 해당 날짜의 각 게임별 최신 예측 리스트 조회
     * 3) gameId 뒤 4글자를 파싱해 원정/홈팀 약어 추출
     * 4) 약어 → 풀네임 매핑
     * 5) LiveWinPredictionResponse 객체로 만들어 반환
     */
    public List<LiveWinPredictionResponse> getLatestPredictionsWithTeams() {
        // 1) 가장 최근 날짜 조회
        Date latestDate = repository.findLatestPredictionDate();
        if (latestDate == null) {
            // 데이터가 하나도 없으면 빈 리스트 반환
            return Collections.emptyList();
        }

        // 2) 해당 날짜에 대한 각 게임별 마지막 예측 조회(DTO 형태)
        List<LiveWinPredictionDto> dtos = repository.findLatestPredictionsByDate((java.sql.Date) latestDate);

        // 3) DTO를 순회하면서 “gameId → awayTeam, homeTeam” 매핑 후 최종 응답 DTO로 변환
        return dtos.stream()
                .map(this::mapToResponseWithTeams)
                .collect(Collectors.toList());
    }

    /**
     * LiveWinPredictionDto에서 gameId를 파싱해
     *  - ‘뒤 4글자’를 두 덩어리(2글자 + 2글자)로 나눠서
     *  - 앞 2글자(원정팀 약어) / 뒤 2글자(홈팀 약어)로 분리
     *  - TeamCodeMapping을 통해 풀네임을 얻어
     * LiveWinPredictionResponse 객체를 생성하여 반환
     */
    private LiveWinPredictionResponse mapToResponseWithTeams(LiveWinPredictionDto dto) {
        String gameId = dto.getGameId();
        if (gameId == null || gameId.length() < 4) {
            // gameId가 형식에 맞지 않으면 UNKNOWN 처리
            return new LiveWinPredictionResponse(
                    gameId,
                    "UNKNOWN",
                    "UNKNOWN",
                    dto.getInning(),
                    dto.getWinProbability(),
                    dto.getHomeAccumScore(),
                    dto.getAwayAccumScore(),
                    dto.getPredictedAt()
            );
        }

        // 예: gameId="20250604HTOB"
        //       0123456789AB index   → 뒤 4글자("HTOB") 기준: length-4 ~ length-2("HT"), length-2~length("OB")
        int len = gameId.length();
        String awayAbbr = gameId.substring(len - 4, len - 2); // e.g. "HT"
        String homeAbbr = gameId.substring(len - 2, len);      // e.g. "OB"

        // TeamCodeMapping에서 풀네임 가져오기
        String awayTeamFull = TeamCodeMapping.getFullName(awayAbbr);
        String homeTeamFull = TeamCodeMapping.getFullName(homeAbbr);

        return new LiveWinPredictionResponse(
                gameId,
                awayTeamFull,
                homeTeamFull,
                dto.getInning(),
                dto.getWinProbability(),
                dto.getHomeAccumScore(),
                dto.getAwayAccumScore(),
                dto.getPredictedAt()
        );
    }
}
