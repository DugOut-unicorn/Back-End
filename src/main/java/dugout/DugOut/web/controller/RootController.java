package dugout.DugOut.web.controller;

import dugout.DugOut.domain.Player;
import dugout.DugOut.domain.User;
import dugout.DugOut.repository.PlayerRepository;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.service.S3Service;
import dugout.DugOut.web.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RootController {

    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final S3Service s3Service;

    @GetMapping("/health")
    public String healthCheck(){
        return "Health Check complete!";
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/playerImage")
    public ApiResponse<String> uploadPlayerImage(
            @RequestParam("playerIdx") Integer playerIdx,
            @RequestParam("file") MultipartFile file) {
        try {
            Player player = playerRepository.findById(playerIdx.longValue())
                    .orElseThrow(() -> new RuntimeException("선수를 찾을 수 없습니다."));

            // 기존 이미지가 있다면 삭제
            if (player.getPlayerImageUrl() != null) {
                s3Service.deleteProfileImage(player.getPlayerImageUrl());
            }

            // 새 이미지 업로드
            String imageUrl = s3Service.uploadPlayerImage(file, playerIdx);

            // 선수 정보 업데이트
            player.setPlayerImageUrl(imageUrl);
            playerRepository.save(player);

            return ApiResponse.success("선수 이미지가 성공적으로 업로드되었습니다.", imageUrl);
        } catch (Exception e) {
            return ApiResponse.error("선수 이미지 업로드에 실패했습니다: " + e.getMessage());
        }
    }
}
