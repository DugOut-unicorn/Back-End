package dugout.DugOut.web.controller;

import dugout.DugOut.domain.MatchingPost;
import dugout.DugOut.domain.User;
import dugout.DugOut.dto.MatchingPostResponseDto;
import dugout.DugOut.dto.UserInfoUpdateRequestDto;
import dugout.DugOut.dto.UserPersonalUpdateRequestDto;
import dugout.DugOut.dto.UserTempResponseDto;
import dugout.DugOut.repository.MatchingPostRepository;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.service.JwtService;
import dugout.DugOut.service.S3Service;
import dugout.DugOut.web.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MatchingPostRepository matchingPostRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final S3Service s3Service;

    private User getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtService.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/myChat")
    public List<MatchingPostResponseDto> getMyChats(HttpServletRequest request) {
        User user = getCurrentUser(request);
        List<MatchingPost> matchingPosts = matchingPostRepository.findByUserIdx(user.getUserIdx());
        return matchingPosts.stream()
                .map(MatchingPostResponseDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/myTemp")
    public UserTempResponseDto getMyTemp(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return new UserTempResponseDto(user);
    }

    @PostMapping("/editInfo")
    public ApiResponse<Void> updateUserInfo(HttpServletRequest request, @RequestBody UserInfoUpdateRequestDto requestDto) {
        User user = getCurrentUser(request);
        
        user.setEmail(requestDto.getEmail());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        
        userRepository.save(user);
        
        return ApiResponse.success("사용자 정보가 성공적으로 업데이트되었습니다.");
    }

    @PostMapping("/editPersonal")
    public ApiResponse<Void> updateUserPersonal(HttpServletRequest request, @RequestBody UserPersonalUpdateRequestDto requestDto) {
        User user = getCurrentUser(request);
        
        user.setNickname(requestDto.getNickname());
        user.setBio(requestDto.getBio());
        user.setCheeringTeamId(requestDto.getCheeringTeamId());
        
        userRepository.save(user);
        
        return ApiResponse.success("사용자 개인정보가 성공적으로 업데이트되었습니다.");
    }

    @PostMapping("/withdraw")
    public ApiResponse<Void> withdrawUser(HttpServletRequest request) {
        User user = getCurrentUser(request);
        
        user.setStatus(0);
        userRepository.save(user);
        
        return ApiResponse.success("회원탈퇴가 성공적으로 처리되었습니다.");
    }

    @PostMapping("/profile-image")
    public ApiResponse<String> uploadProfileImage(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file) {
        try {
            User user = getCurrentUser(request);
            
            // 기존 프로필 이미지가 있다면 삭제
            if (user.getProfileImageUrl() != null) {
                s3Service.deleteProfileImage(user.getProfileImageUrl());
            }

            // 새 프로필 이미지 업로드
            String imageUrl = s3Service.uploadProfileImage(file, user.getEmail());
            
            // 사용자 정보 업데이트
            user.setProfileImageUrl(imageUrl);
            userRepository.save(user);

            return ApiResponse.success("프로필 이미지가 성공적으로 업로드되었습니다.", imageUrl);
        } catch (Exception e) {
            log.error("프로필 이미지 업로드 실패: {}", e.getMessage());
            return ApiResponse.error("프로필 이미지 업로드에 실패했습니다.");
        }
    }
} 