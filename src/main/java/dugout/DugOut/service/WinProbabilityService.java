package dugout.DugOut.service;

import dugout.DugOut.domain.Game;
import dugout.DugOut.domain.WinProbability;
import dugout.DugOut.repository.GameRepository;
import dugout.DugOut.repository.WinProbabilityRepository;
import dugout.DugOut.web.dto.response.WinProbabilityDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class WinProbabilityService {
    private final GameRepository gameRepo;
    private final WinProbabilityRepository probRepo;

    public WinProbabilityService(
            GameRepository gameRepo,
            WinProbabilityRepository probRepo
    ) {
        this.gameRepo = gameRepo;
        this.probRepo = probRepo;
    }

    // 1. 팀 idx → 팀 이름 매핑표
    private static final Map<Integer, String> TEAM_NAME_MAP = Map.of(
            1, "LG",
            2, "SSG",
            3, "삼성",
            4, "KT",
            5, "롯데",
            6, "NC",
            7, "두산",
            8, "키움",
            9, "KIA",
            10,"한화"
    );

    /**
     * 날짜 하나만 받아서
     * 1) 해당 날짜의 모든 경기 조회
     * 2) 각각 홈/원정 팀으로 승률 조회
     * 3) WinRateDto 리스트로 반환
     */
    public List<WinProbabilityDto> getWinRatesByDate(LocalDate date) {
        List<Game> games = gameRepo.findGamesByDate(date);

        return games.stream()
                .map(game -> {
                    // 2. idx → 이름으로 변환
                    String homeName = TEAM_NAME_MAP.get(game.getHomeTeamIdx());
                    String awayName = TEAM_NAME_MAP.get(game.getAwayTeamIdx());

                    // 3. 이름으로 승률 조회
                    Double winRate = probRepo
                            .findByTeam1AndTeam2(homeName, awayName)
                            .map(WinProbability::getWinProbability)
                            .orElse(0.0);

                    return new WinProbabilityDto(homeName, awayName, winRate);
                })
                .collect(Collectors.toList());
    }
}