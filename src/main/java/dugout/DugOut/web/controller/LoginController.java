package dugout.DugOut.web.controller;

import dugout.DugOut.domain.User;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.service.JwtService;
import dugout.DugOut.web.dto.request.NicknameUpdateRequest;
import dugout.DugOut.web.dto.request.CheeringTeamUpdateRequest;
import dugout.DugOut.web.dto.response.ApiResponse;
import dugout.DugOut.web.dto.response.ErrorResponse;
import dugout.DugOut.web.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Tag(name = "/login", description = "회원가입/로그인 관련 API")
public class LoginController {
    
    private final UserRepository userRepository;
    private final JwtService jwtService;
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        return bearerToken.substring(7);
    }

    private User getUserFromToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        String email = jwtService.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
    
    @Operation(
        summary = "회원가입 시 닉네임 받기",
        description = "사용자의 닉네임을 업데이트합니다.",
        parameters = {
            @Parameter(
                name = "Authorization",
                description = "JWT 토큰",
                required = true,
                in = ParameterIn.HEADER,
                schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "닉네임 업데이트 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "닉네임이 성공적으로 업데이트되었습니다."
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
                        name = "닉네임 누락",
                        value = """
                            {
                                "success": false,
                                "message": "닉네임은 필수 입력값입니다."
                            }"""
                    ),
                    @ExampleObject(
                        name = "닉네임 중복",
                        value = """
                            {
                                "success": false,
                                "message": "이미 사용 중인 닉네임입니다. 다른 닉네임을 입력해주세요."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "인증 실패",
                        value = """
                            {
                                "success": false,
                                "message": "유효하지 않은 토큰입니다."
                            }"""
                    )
                }
            )
        )
    })
    @PostMapping("/nickname")
    public ResponseEntity<ApiResponse<Void>> saveNickname(
            HttpServletRequest request,
            @Parameter(
                description = "닉네임 업데이트 요청 정보",
                required = true,
                schema = @Schema(
                    implementation = NicknameUpdateRequest.class,
                    example = """
                        {
                            "nickname": "새로운닉네임"
                        }"""
                )
            )
            @RequestBody NicknameUpdateRequest nicknameRequest) {
        try {
            User user = getUserFromToken(request);
            
            if (nicknameRequest.getNickname() == null || nicknameRequest.getNickname().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(ErrorResponse.NICKNAME_REQUIRED));
            }

            String newNickname = nicknameRequest.getNickname().trim();
            
            // 현재 사용자의 닉네임과 동일한 경우는 중복으로 처리하지 않음
            if (!newNickname.equals(user.getNickname())) {
                // 다른 사용자들의 닉네임과 중복 검사
                boolean isNicknameExists = userRepository.existsByNickname(newNickname);
                if (isNicknameExists) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("이미 사용 중인 닉네임입니다. 다른 닉네임을 입력해주세요."));
                }
            }
            
            user.setNickname(newNickname);
            userRepository.save(user);
            
            return ResponseEntity.ok(ApiResponse.success(SuccessResponse.NICKNAME_UPDATED));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(
        summary = "회원가입 시 응원팀 받기",
        description = "사용자의 응원팀을 업데이트합니다.",
        parameters = {
            @Parameter(
                name = "Authorization",
                description = "JWT 토큰",
                required = true,
                in = ParameterIn.HEADER,
                schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "응원팀 업데이트 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "성공 응답",
                        value = """
                            {
                                "success": true,
                                "message": "응원팀이 성공적으로 업데이트되었습니다."
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
                        name = "응원팀 누락",
                        value = """
                            {
                                "success": false,
                                "message": "응원팀은 필수 선택값입니다."
                            }"""
                    ),
                    @ExampleObject(
                        name = "응원팀 범위 오류",
                        value = """
                            {
                                "success": false,
                                "message": "응원팀 ID는 1에서 10 사이의 값이어야 합니다."
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "인증 실패",
                        value = """
                            {
                                "success": false,
                                "message": "유효하지 않은 토큰입니다."
                            }"""
                    )
                }
            )
        )
    })
    @PostMapping("/cheeringTeamId")
    public ResponseEntity<ApiResponse<Void>> saveCheeringTeamId(
            HttpServletRequest request,
            @Parameter(
                description = "응원팀 업데이트 요청 정보",
                required = true,
                schema = @Schema(
                    implementation = CheeringTeamUpdateRequest.class,
                    example = """
                        {
                            "cheeringTeamId": 1
                        }"""
                )
            )
            @RequestBody CheeringTeamUpdateRequest cheeringTeamRequest) {
        try {
            User user = getUserFromToken(request);
            
            if (cheeringTeamRequest.getCheeringTeamId() == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(ErrorResponse.CHEERING_TEAM_REQUIRED));
            }
            
            if (cheeringTeamRequest.getCheeringTeamId() < 1 || cheeringTeamRequest.getCheeringTeamId() > 10) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(ErrorResponse.CHEERING_TEAM_RANGE));
            }
            
            user.setCheeringTeamId(cheeringTeamRequest.getCheeringTeamId());
            userRepository.save(user);
            
            return ResponseEntity.ok(ApiResponse.success(SuccessResponse.CHEERING_TEAM_UPDATED));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

//    @GetMapping("/nickname")
//    public ResponseEntity<ApiResponse<Map<String, Object>>> getNickname(HttpServletRequest request) {
//        try {
//            User user = getUserFromToken(request);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("nickname", user.getNickname());
//            response.put("cheeringTeamId", user.getCheeringTeamId());
//
//            return ResponseEntity.ok(ApiResponse.success("사용자 정보를 성공적으로 조회했습니다.", response));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest()
//                    .body(ApiResponse.error(e.getMessage()));
//        }
//    }
}
