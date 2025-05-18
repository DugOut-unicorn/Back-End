package dugout.DugOut.web.controller;

import dugout.DugOut.domain.HitterStat;
import dugout.DugOut.domain.PitcherStat;
import dugout.DugOut.domain.Player;
import dugout.DugOut.domain.TeamRecord;
import dugout.DugOut.domain.DefenseStat;
import dugout.DugOut.repository.HitterStatRepository;
import dugout.DugOut.repository.PitcherStatRepository;
import dugout.DugOut.repository.PlayerRepository;
import dugout.DugOut.repository.TeamRecordRepository;
import dugout.DugOut.repository.DefenseStatRepository;
import dugout.DugOut.web.dto.response.ApiResponse;
import dugout.DugOut.web.dto.response.TopHitterResponseDto;
import dugout.DugOut.web.dto.response.TopRbiHitterResponseDto;
import dugout.DugOut.web.dto.response.TopGameHitterResponseDto;
import dugout.DugOut.web.dto.response.TopPaHitterResponseDto;
import dugout.DugOut.web.dto.response.TopAbHitterResponseDto;
import dugout.DugOut.web.dto.response.TopRHitterResponseDto;
import dugout.DugOut.web.dto.response.TopHHitterResponseDto;
import dugout.DugOut.web.dto.response.TeamRecordResponseDto;
import dugout.DugOut.web.dto.response.TopEraPitcherResponseDto;
import dugout.DugOut.web.dto.response.TopSoPitcherResponseDto;
import dugout.DugOut.web.dto.response.TopErPitcherResponseDto;
import dugout.DugOut.web.dto.response.TopSvPitcherResponseDto;
import dugout.DugOut.web.dto.response.TopWPitcherResponseDto;
import dugout.DugOut.web.dto.response.PlayerResponseDto;
import dugout.DugOut.web.dto.response.PersonalRecordResponseDto;
import dugout.DugOut.web.dto.response.TeamRankResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Record", description = "선수 기록 관련 API /pitcherStat: 투수, /hitterStat: 타자")
@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {

    private final HitterStatRepository hitterStatRepository;
    private final PitcherStatRepository pitcherStatRepository;
    private final PlayerRepository playerRepository;
    private final TeamRecordRepository teamRecordRepository;
    private final DefenseStatRepository defenseStatRepository;

    @Operation(
        summary = "타율 상위 3명 조회",
        description = "특정 팀의 타율 상위 3명의 선수 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    implementation = ApiResponse.class,
                    subTypes = {TopHitterResponseDto.class}
                ),
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "타율 상위 3명의 선수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "playerName": "김현수",
                                        "avg": 0.345,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    },
                                    {
                                        "playerName": "이정후",
                                        "avg": 0.333,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/2/image.jpg"
                                    },
                                    {
                                        "playerName": "박병호",
                                        "avg": 0.321,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/3/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "선수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 선수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/hitterStat/AVG")
    public ApiResponse<List<TopHitterResponseDto>> getTopHittersByAvg(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<HitterStat> topHitterStats = hitterStatRepository.findTop3ByTeamIdxOrderByAvgDesc(teamIdx);

            List<TopHitterResponseDto> response = topHitterStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx().longValue())
                                .orElseThrow(() -> new RuntimeException("선수를 찾을 수 없습니다."));
                        return new TopHitterResponseDto(
                                player.getPlayerName(),
                                stat.getAvg(),
                                player.getPlayerImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("타율 상위 3명의 선수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("선수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "타점 상위 3명 조회",
        description = "특정 팀의 타점 상위 3명의 선수 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    implementation = ApiResponse.class,
                    subTypes = {TopRbiHitterResponseDto.class}
                ),
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "타점 상위 3명의 선수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "playerName": "김현수",
                                        "rbi": 85,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    },
                                    {
                                        "playerName": "이정후",
                                        "rbi": 78,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/2/image.jpg"
                                    },
                                    {
                                        "playerName": "박병호",
                                        "rbi": 72,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/3/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "선수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 선수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/hitterStat/RBI")
    public ApiResponse<List<TopRbiHitterResponseDto>> getTopHittersByRbi(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<HitterStat> topHitterStats = hitterStatRepository.findTop3ByTeamIdxOrderByRbiDesc(teamIdx);

            List<TopRbiHitterResponseDto> response = topHitterStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx().longValue())
                                .orElseThrow(() -> new RuntimeException("선수를 찾을 수 없습니다."));
                        return new TopRbiHitterResponseDto(
                                player.getPlayerName(),
                                stat.getRbi(),
                                player.getPlayerImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("타점 상위 3명의 선수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("선수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "게임 수 상위 3명 조회",
        description = "특정 팀의 게임 수 상위 3명의 선수 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "게임 수 상위 3명의 선수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "playerName": "김현수",
                                        "game": 120,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "선수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 선수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/hitterStat/game")
    public ApiResponse<List<TopGameHitterResponseDto>> getTopHittersByGame(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<HitterStat> topHitterStats = hitterStatRepository.findTop3ByTeamIdxOrderByGDesc(teamIdx);

            List<TopGameHitterResponseDto> response = topHitterStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx().longValue())
                                .orElseThrow(() -> new RuntimeException("선수를 찾을 수 없습니다."));
                        return new TopGameHitterResponseDto(
                                player.getPlayerName(),
                                stat.getG(),
                                player.getPlayerImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("게임 수 상위 3명의 선수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("선수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "타석 수 상위 3명 조회",
        description = "특정 팀의 타석 수 상위 3명의 선수 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "타석 수 상위 3명의 선수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "playerName": "김현수",
                                        "pa": 450,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "선수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 선수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/hitterStat/PA")
    public ApiResponse<List<TopPaHitterResponseDto>> getTopHittersByPa(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<HitterStat> topHitterStats = hitterStatRepository.findTop3ByTeamIdxOrderByPaDesc(teamIdx);

            List<TopPaHitterResponseDto> response = topHitterStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx().longValue())
                                .orElseThrow(() -> new RuntimeException("선수를 찾을 수 없습니다."));
                        return new TopPaHitterResponseDto(
                                player.getPlayerName(),
                                stat.getPa(),
                                player.getPlayerImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("타석 수 상위 3명의 선수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("선수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "타수 상위 3명 조회",
        description = "특정 팀의 타수 상위 3명의 선수 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "타수 상위 3명의 선수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "playerName": "김현수",
                                        "ab": 400,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "선수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 선수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/hitterStat/AB")
    public ApiResponse<List<TopAbHitterResponseDto>> getTopHittersByAb(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<HitterStat> topHitterStats = hitterStatRepository.findTop3ByTeamIdxOrderByAbDesc(teamIdx);

            List<TopAbHitterResponseDto> response = topHitterStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx().longValue())
                                .orElseThrow(() -> new RuntimeException("선수를 찾을 수 없습니다."));
                        return new TopAbHitterResponseDto(
                                player.getPlayerName(),
                                stat.getAb(),
                                player.getPlayerImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("타수 상위 3명의 선수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("선수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "득점 상위 3명 조회",
        description = "특정 팀의 득점 상위 3명의 선수 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "득점 상위 3명의 선수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "playerName": "김현수",
                                        "r": 80,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "선수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 선수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/hitterStat/R")
    public ApiResponse<List<TopRHitterResponseDto>> getTopHittersByR(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<HitterStat> topHitterStats = hitterStatRepository.findTop3ByTeamIdxOrderByRDesc(teamIdx);

            List<TopRHitterResponseDto> response = topHitterStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx().longValue())
                                .orElseThrow(() -> new RuntimeException("선수를 찾을 수 없습니다."));
                        return new TopRHitterResponseDto(
                                player.getPlayerName(),
                                stat.getR(),
                                player.getPlayerImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("득점 상위 3명의 선수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("선수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "안타 상위 3명 조회",
        description = "특정 팀의 안타 상위 3명의 선수 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "안타 상위 3명의 선수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "playerName": "김현수",
                                        "h": 150,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "선수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 선수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/hitterStat/H")
    public ApiResponse<List<TopHHitterResponseDto>> getTopHittersByH(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<HitterStat> topHitterStats = hitterStatRepository.findTop3ByTeamIdxOrderByHDesc(teamIdx);

            List<TopHHitterResponseDto> response = topHitterStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx().longValue())
                                .orElseThrow(() -> new RuntimeException("선수를 찾을 수 없습니다."));
                        return new TopHHitterResponseDto(
                                player.getPlayerName(),
                                stat.getH(),
                                player.getPlayerImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("안타 상위 3명의 선수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("선수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "팀 순위 및 성적 조회",
        description = "특정 팀의 현재 순위와 성적 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "팀 순위 및 성적 정보를 조회했습니다.",
                                "data": {
                                    "rank": 3,
                                    "game": 120,
                                    "win": 65,
                                    "draw": 5,
                                    "lose": 50,
                                    "winRate": 0.542
                                }
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "팀 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "팀 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "팀 정보 조회에 실패했습니다: 팀을 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/team")
    public ApiResponse<TeamRecordResponseDto> getTeamRecord(@RequestParam("team_idx") Integer teamIdx) {
        try {
            // 가장 최신 날짜 조회
            LocalDate latestDate = teamRecordRepository.findLatestDate();
            if (latestDate == null) {
                return ApiResponse.error("팀 정보 조회에 실패했습니다: 기록이 없습니다.");
            }

            // 해당 날짜의 모든 팀 기록을 승률 내림차순으로 조회
            List<TeamRecord> allTeamRecords = teamRecordRepository.findAllByDateOrderByWinRateDesc(latestDate);
            
            // 요청한 팀의 기록 조회
            TeamRecord teamRecord = teamRecordRepository.findByDateAndTeamIdx(latestDate, teamIdx);
            if (teamRecord == null) {
                return ApiResponse.error("팀 정보 조회에 실패했습니다: 팀을 찾을 수 없습니다.");
            }

            // 순위 계산 (승률이 같은 경우 동순위)
            int rank = 1;
            BigDecimal currentWinRate = new BigDecimal("-1");
            int sameRankCount = 0;
            
            for (TeamRecord record : allTeamRecords) {
                if (record.getWinRate().compareTo(currentWinRate) != 0) {
                    rank += sameRankCount;
                    currentWinRate = record.getWinRate();
                    sameRankCount = 0;
                }
                
                if (record.getTeamIdx().equals(teamIdx)) {
                    break;
                }
                
                sameRankCount++;
            }

            TeamRecordResponseDto response = new TeamRecordResponseDto(
                rank,
                teamRecord.getGame(),
                teamRecord.getWin(),
                teamRecord.getDraw(),
                teamRecord.getLose(),
                teamRecord.getWinRate().doubleValue()
            );

            return ApiResponse.success("팀 순위 및 성적 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("팀 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "평균자책점 하위 3명 조회",
        description = "특정 팀의 평균자책점 하위 3명의 투수 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "평균자책점 하위 3명의 투수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "playerName": "김현수",
                                        "era": 2.50,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "투수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "투수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "투수 정보 조회에 실패했습니다: 투수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/pitcherStat/ERA")
    public ApiResponse<List<TopEraPitcherResponseDto>> getTopPitchersByEra(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<PitcherStat> topPitcherStats = pitcherStatRepository.findTop3ByTeamIdxOrderByEraAsc(teamIdx);

            List<TopEraPitcherResponseDto> response = topPitcherStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx().longValue())
                                .orElseThrow(() -> new RuntimeException("투수를 찾을 수 없습니다."));
                        return new TopEraPitcherResponseDto(
                                player.getPlayerName(),
                                stat.getEra(),
                                player.getPlayerImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("평균자책점 하위 3명의 투수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("투수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "삼진 수 상위 3명 조회",
        description = "특정 팀의 삼진 수 상위 3명의 투수 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "삼진 수 상위 3명의 투수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "playerName": "김현수",
                                        "so": 150,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "투수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "투수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "투수 정보 조회에 실패했습니다: 투수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/pitcherStat/SO")
    public ApiResponse<List<TopSoPitcherResponseDto>> getTopPitchersBySo(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<PitcherStat> topPitcherStats = pitcherStatRepository.findTop3ByTeamIdxOrderBySoDesc(teamIdx);

            List<TopSoPitcherResponseDto> response = topPitcherStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx().longValue())
                                .orElseThrow(() -> new RuntimeException("투수를 찾을 수 없습니다."));
                        return new TopSoPitcherResponseDto(
                                player.getPlayerName(),
                                stat.getSo(),
                                player.getPlayerImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("삼진 수 상위 3명의 투수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("투수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "평균자책 하위 3명 조회",
        description = "특정 팀의 평균자책 하위 3명의 투수 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "평균자책 하위 3명의 투수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "playerName": "김현수",
                                        "er": 25,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "투수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "투수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "투수 정보 조회에 실패했습니다: 투수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/pitcherStat/ER")
    public ApiResponse<List<TopErPitcherResponseDto>> getTopPitchersByEr(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<PitcherStat> topPitcherStats = pitcherStatRepository.findTop3ByTeamIdxOrderByErAsc(teamIdx);

            List<TopErPitcherResponseDto> response = topPitcherStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx().longValue())
                                .orElseThrow(() -> new RuntimeException("투수를 찾을 수 없습니다."));
                        return new TopErPitcherResponseDto(
                                player.getPlayerName(),
                                stat.getEr(),
                                player.getPlayerImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("평균자책 하위 3명의 투수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("투수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "세이브 상위 3명 조회",
        description = "특정 팀의 세이브 상위 3명의 투수 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "세이브 상위 3명의 투수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "playerName": "김현수",
                                        "sv": 30,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "투수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "투수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "투수 정보 조회에 실패했습니다: 투수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/pitcherStat/SV")
    public ApiResponse<List<TopSvPitcherResponseDto>> getTopPitchersBySv(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<PitcherStat> topPitcherStats = pitcherStatRepository.findTop3ByTeamIdxOrderBySvDesc(teamIdx);

            List<TopSvPitcherResponseDto> response = topPitcherStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx().longValue())
                                .orElseThrow(() -> new RuntimeException("투수를 찾을 수 없습니다."));
                        return new TopSvPitcherResponseDto(
                                player.getPlayerName(),
                                stat.getSv(),
                                player.getPlayerImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("세이브 상위 3명의 투수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("투수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "승리 상위 3명 조회",
        description = "특정 팀의 승리 상위 3명의 투수 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "승리 상위 3명의 투수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "playerName": "김현수",
                                        "w": 15,
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "투수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "투수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "투수 정보 조회에 실패했습니다: 투수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/pitcherStat/W")
    public ApiResponse<List<TopWPitcherResponseDto>> getTopPitchersByW(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<PitcherStat> topPitcherStats = pitcherStatRepository.findTop3ByTeamIdxOrderByWDesc(teamIdx);

            List<TopWPitcherResponseDto> response = topPitcherStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx().longValue())
                                .orElseThrow(() -> new RuntimeException("투수를 찾을 수 없습니다."));
                        return new TopWPitcherResponseDto(
                                player.getPlayerName(),
                                stat.getW(),
                                player.getPlayerImageUrl()
                        );
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("승리 상위 3명의 투수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("투수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "전체 선수 조회",
        description = "특정 팀의 전체 선수 정보를 등번호 순으로 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "전체 선수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "backNumber": 1,
                                        "playerName": "김현수",
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "선수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 선수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/team/all")
    public ApiResponse<List<PlayerResponseDto>> getAllPlayers(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<Player> players = playerRepository.findTop20ByTeamIdxAndBackNumberIsNotNullOrderByBackNumberAsc(teamIdx);

            List<PlayerResponseDto> response = players.stream()
                    .map(player -> new PlayerResponseDto(
                            player.getBackNumber(),
                            player.getPlayerName(),
                            player.getPlayerImageUrl()
                    ))
                    .collect(Collectors.toList());

            return ApiResponse.success("전체 선수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("선수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "투수 조회",
        description = "특정 팀의 투수 정보를 등번호 순으로 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "투수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "backNumber": 1,
                                        "playerName": "김현수",
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "선수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 선수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/team/pitcher")
    public ApiResponse<List<PlayerResponseDto>> getPitchers(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<Player> players = playerRepository.findTop10PitchersByTeamIdxAndPositionAndBackNumberIsNotNullOrderByBackNumberAsc(teamIdx, "투수");

            List<PlayerResponseDto> response = players.stream()
                    .map(player -> new PlayerResponseDto(
                            player.getBackNumber(),
                            player.getPlayerName(),
                            player.getPlayerImageUrl()
                    ))
                    .collect(Collectors.toList());

            return ApiResponse.success("투수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("선수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "내야수 조회",
        description = "특정 팀의 내야수 정보를 등번호 순으로 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "내야수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "backNumber": 1,
                                        "playerName": "김현수",
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "선수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 선수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/team/infielder")
    public ApiResponse<List<PlayerResponseDto>> getInfielders(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<Player> players = playerRepository.findTop10InfieldersByTeamIdxAndPositionAndBackNumberIsNotNullOrderByBackNumberAsc(teamIdx, "내야수");

            List<PlayerResponseDto> response = players.stream()
                    .map(player -> new PlayerResponseDto(
                            player.getBackNumber(),
                            player.getPlayerName(),
                            player.getPlayerImageUrl()
                    ))
                    .collect(Collectors.toList());

            return ApiResponse.success("내야수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("선수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "외야수 조회",
        description = "특정 팀의 외야수 정보를 등번호 순으로 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "외야수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "backNumber": 1,
                                        "playerName": "김현수",
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "선수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 선수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/team/outfielder")
    public ApiResponse<List<PlayerResponseDto>> getOutfielders(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<Player> players = playerRepository.findTop10OutfieldersByTeamIdxAndPositionAndBackNumberIsNotNullOrderByBackNumberAsc(teamIdx, "외야수");

            List<PlayerResponseDto> response = players.stream()
                    .map(player -> new PlayerResponseDto(
                            player.getBackNumber(),
                            player.getPlayerName(),
                            player.getPlayerImageUrl()
                    ))
                    .collect(Collectors.toList());

            return ApiResponse.success("외야수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("선수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "포수 조회",
        description = "특정 팀의 포수 정보를 등번호 순으로 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "3")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "포수 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "backNumber": 1,
                                        "playerName": "김현수",
                                        "playerImageUrl": "https://dugout-profile.s3.ap-northeast-2.amazonaws.com/players/1/image.jpg"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 잘못된 팀 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "선수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 선수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/team/catcher")
    public ApiResponse<List<PlayerResponseDto>> getCatchers(@RequestParam("team_idx") Integer teamIdx) {
        try {
            List<Player> players = playerRepository.findTop10CatchersByTeamIdxAndPositionAndBackNumberIsNotNullOrderByBackNumberAsc(teamIdx, "포수");

            List<PlayerResponseDto> response = players.stream()
                    .map(player -> new PlayerResponseDto(
                            player.getBackNumber(),
                            player.getPlayerName(),
                            player.getPlayerImageUrl()
                    ))
                    .collect(Collectors.toList());

            return ApiResponse.success("포수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("선수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "선수 개인 정보 및 기록 조회",
        description = "선수의 개인 정보와 포지션별 기록을 조회합니다.",
        parameters = {
            @Parameter(
                name = "player_idx",
                description = "선수 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "744")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "선수 정보를 조회했습니다.",
                                "data": {
                                    "playerName": "김현수",
                                    "teamIdx": 1,
                                    "backNumber": 10,
                                    "position": "투수",
                                    "birthday": "1990-01-01",
                                    "heightWeight": "180cm/80kg",
                                    "career": "서울고-한양대",
                                    "playerImageUrl": "https://example.com/image.jpg",
                                    "pitcherStat": {
                                        "era": 2.50,
                                        "w": 15,
                                        "l": 5,
                                        "sv": 0,
                                        "so": 150
                                    },
                                    "hasRecord": true
                                }
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 잘못된 선수 인덱스입니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "선수 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "선수 정보 조회에 실패했습니다: 선수를 찾을 수 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/personal")
    public ApiResponse<PersonalRecordResponseDto> getPersonalRecord(@RequestParam("player_idx") Long playerIdx) {
        try {
            // 선수 기본 정보 조회
            Player player = playerRepository.findById(playerIdx)
                    .orElseThrow(() -> new RuntimeException("선수를 찾을 수 없습니다."));

            PersonalRecordResponseDto response = new PersonalRecordResponseDto();
            response.setPlayerIdx(player.getPlayerIdx());
            response.setPlayerName(player.getPlayerName());
            response.setBackNumber(player.getBackNumber());
            response.setPosition(player.getPosition());
            response.setBirthday(player.getBirthday());
            response.setHeightWeight(player.getHeightWeight());
            response.setCareer(player.getCareer());
            response.setPlayerImageUrl(player.getPlayerImageUrl());

            // 포지션별 기록 조회
            if ("투수".equals(player.getPosition())) {
                PitcherStat pitcherStat = pitcherStatRepository.findByPlayerIdx(playerIdx.intValue());
                if (pitcherStat != null) {
                    response.setPitcherStat(pitcherStat);
                    response.setHasRecord(true);
                }
            } else if ("타자".equals(player.getPosition())) {
                HitterStat hitterStat = hitterStatRepository.findByPlayerIdx(playerIdx.intValue());
                if (hitterStat != null) {
                    response.setHitterStat(hitterStat);
                    response.setHasRecord(true);
                }
            } else {
                DefenseStat defenseStat = defenseStatRepository.findByPlayerIdx(playerIdx.intValue());
                if (defenseStat != null) {
                    response.setDefenseStat(defenseStat);
                    response.setHasRecord(true);
                }
            }

            return ApiResponse.success("선수 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("선수 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
        summary = "팀 순위 조회",
        description = "최신 날짜 기준으로 10개 팀의 순위 정보를 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "팀 순위 정보를 조회했습니다.",
                                "data": [
                                    {
                                        "teamIdx": 1,
                                        "game": 120,
                                        "win": 65,
                                        "draw": 5,
                                        "lose": 50,
                                        "winRate": 0.542,
                                        "gameGap": 0.0,
                                        "streak": "W3",
                                        "recentTen": "7승3패"
                                    }
                                ]
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "기록을 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "실패 응답",
                        value = """
                            {
                                "success": false,
                                "message": "팀 순위 정보 조회에 실패했습니다: 기록이 없습니다."
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping("/teamRank")
    public ApiResponse<List<TeamRankResponseDto>> getTeamRank() {
        try {
            List<TeamRecord> teamRecords = teamRecordRepository.findTop10ByLatestDateOrderByWinRateDesc();
            
            if (teamRecords.isEmpty()) {
                return ApiResponse.error("팀 순위 정보 조회에 실패했습니다: 기록이 없습니다.");
            }

            List<TeamRankResponseDto> response = teamRecords.stream()
                    .map(record -> new TeamRankResponseDto(
                            record.getTeamIdx(),
                            record.getGame(),
                            record.getWin(),
                            record.getDraw(),
                            record.getLose(),
                            record.getWinRate().doubleValue(),
                            record.getGameGap().doubleValue(),
                            record.getStreak(),
                            record.getRecentTen()
                    ))
                    .toList();

            return ApiResponse.success("팀 순위 정보를 조회했습니다.", response);
        } catch (Exception e) {
            return ApiResponse.error("팀 순위 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }
} 