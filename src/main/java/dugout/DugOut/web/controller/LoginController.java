package dugout.DugOut.web.controller;

import dugout.DugOut.domain.User;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.web.dto.request.NicknameRequest;
import dugout.DugOut.web.dto.response.ApiResponse;
import dugout.DugOut.web.dto.response.ErrorResponse;
import dugout.DugOut.web.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Tag(name = "/login", description = "회원가입/로그인 관련 API")
public class LoginController {
    
    private final UserRepository userRepository;
    
    @Operation(summary = "회원가입 시 닉네임, 응원팀 받기", description = "사용자의 닉네임과 응원팀을 업데이트합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "닉네임 업데이트 성공", 
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", 
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/nickname")
    public ResponseEntity<ApiResponse<Void>> saveNickname(
            @Parameter(description = "닉네임 업데이트 요청 정보", required = true)
            @RequestBody NicknameRequest request) {
        if (request.getUserIdx() == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ErrorResponse.USER_ID_REQUIRED));
        }
        
        if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ErrorResponse.NICKNAME_REQUIRED));
        }
        
        if (request.getCheeringTeamId() == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ErrorResponse.CHEERING_TEAM_REQUIRED));
        }
        
        if (request.getCheeringTeamId() < 1 || request.getCheeringTeamId() > 10) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ErrorResponse.CHEERING_TEAM_RANGE));
        }
        
        User user = userRepository.findById(request.getUserIdx())
                .orElse(null);
                
        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ErrorResponse.USER_NOT_FOUND));
        }
        
        user.setNickname(request.getNickname().trim());
        user.setCheeringTeamId(request.getCheeringTeamId());
        
        userRepository.save(user);
        
        return ResponseEntity.ok(ApiResponse.success(SuccessResponse.NICKNAME_UPDATED));
    }
}