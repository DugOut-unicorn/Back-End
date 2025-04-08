package dugout.DugOut.web.controller;

import dugout.DugOut.domain.User;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.web.dto.request.NicknameRequest;
import dugout.DugOut.web.dto.response.ApiResponse;
import dugout.DugOut.web.dto.response.ErrorResponse;
import dugout.DugOut.web.dto.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    
    private final UserRepository userRepository;
    
    @PostMapping("/nickname")
    public ResponseEntity<ApiResponse<Void>> saveNickname(@RequestBody NicknameRequest request) {
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
