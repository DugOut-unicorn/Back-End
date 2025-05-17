package dugout.DugOut.web.controller;

import dugout.DugOut.domain.HitterStat;
import dugout.DugOut.domain.Player;
import dugout.DugOut.repository.HitterStatRepository;
import dugout.DugOut.repository.PlayerRepository;
import dugout.DugOut.web.dto.response.ApiResponse;
import dugout.DugOut.web.dto.response.TopHitterResponseDto;
import dugout.DugOut.web.dto.response.TopRbiHitterResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Record", description = "선수 기록 관련 API")
@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {

    private final HitterStatRepository hitterStatRepository;
    private final PlayerRepository playerRepository;

    @Operation(
        summary = "타율 상위 3명 조회",
        description = "특정 팀의 타율 상위 3명의 선수 정보를 조회합니다.",
        parameters = {
            @Parameter(
                name = "team_idx",
                description = "팀 인덱스",
                required = true,
                schema = @Schema(type = "integer", example = "1")
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
            // 해당 팀의 타율 상위 3명의 선수 통계 조회
            List<HitterStat> topHitterStats = hitterStatRepository.findTop3ByTeamIdxOrderByAvgDesc(teamIdx);

            // 선수 정보와 통계 정보를 조합하여 응답 DTO 생성
            List<TopHitterResponseDto> response = topHitterStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx())
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
                schema = @Schema(type = "integer", example = "1")
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
            // 해당 팀의 타점 상위 3명의 선수 통계 조회
            List<HitterStat> topHitterStats = hitterStatRepository.findTop3ByTeamIdxOrderByRbiDesc(teamIdx);

            // 선수 정보와 통계 정보를 조합하여 응답 DTO 생성
            List<TopRbiHitterResponseDto> response = topHitterStats.stream()
                    .map(stat -> {
                        Player player = playerRepository.findById(stat.getPlayerIdx())
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
} 