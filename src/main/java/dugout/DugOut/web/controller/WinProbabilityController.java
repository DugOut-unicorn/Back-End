package dugout.DugOut.web.controller;

import dugout.DugOut.service.LiveWinPredictionService;
import dugout.DugOut.service.WinProbabilityService;
import dugout.DugOut.web.dto.LiveWinPredictionDto;
import dugout.DugOut.web.dto.response.LiveWinPredictionResponse;
import dugout.DugOut.web.dto.response.WinProbabilityDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/win-rates")
public class WinProbabilityController {
    private final WinProbabilityService service;
    private final LiveWinPredictionService liveWinPredictionService;

    public WinProbabilityController(WinProbabilityService service, LiveWinPredictionService liveWinPredictionService) {
        this.service = service;
        this.liveWinPredictionService = liveWinPredictionService;
    }


    @Operation(
            summary = "특정 날짜의 경기들 승률 예측값 조회"
    )
    @GetMapping
    public ResponseEntity<List<WinProbabilityDto>> getByDate(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        List<WinProbabilityDto> list = service.getWinRatesByDate(date);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "실시간 예측값 반환")
    @GetMapping("/live-prediction")
    public ResponseEntity<List<LiveWinPredictionResponse>> getLatestPredictionsWithTeams() {
        List<LiveWinPredictionResponse> results = liveWinPredictionService.getLatestPredictionsWithTeams();
        return ResponseEntity.ok(results);
    }
}
