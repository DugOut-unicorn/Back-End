package dugout.DugOut.web.controller;

import dugout.DugOut.domain.MatchingPost;
import dugout.DugOut.domain.User;
import dugout.DugOut.dto.MatchingPostResponseDto;
import dugout.DugOut.dto.UserTempResponseDto;
import dugout.DugOut.repository.MatchingPostRepository;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.service.JwtService;
import dugout.DugOut.service.MatchingPostService;
import dugout.DugOut.service.S3Service;
import dugout.DugOut.web.dto.request.UserInfoUpdateRequestDto;
import dugout.DugOut.web.dto.request.UserPersonalUpdateRequestDto;
import dugout.DugOut.web.dto.response.ApiResponse;
import dugout.DugOut.web.dto.response.MypageMatchingPostsResponse;
import dugout.DugOut.web.dto.response.ToggleMatchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
    private final MatchingPostService matchingPostService;

    private User getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtService.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/myPost")
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
        try {
            User user = getCurrentUser(request);

            // 생년월일 검증 및 변환
            if (requestDto.getBirth() != null) {
                String birth = requestDto.getBirth();
                if (birth.length() != 8) {
                    return ApiResponse.error("생년월일은 8자리 숫자여야 합니다.");
                }

                int year = Integer.parseInt(birth.substring(0, 4));
                int month = Integer.parseInt(birth.substring(4, 6));
                int day = Integer.parseInt(birth.substring(6, 8));

                try {
                    LocalDate birthDate = LocalDate.of(year, month, day);
                    user.setBirth(birthDate);
                } catch (Exception e) {
                    return ApiResponse.error("유효하지 않은 생년월일입니다.");
                }
            }

            // 성별 업데이트
            if (requestDto.getGender() != null) {
                String gender = requestDto.getGender();
                if (!gender.matches("^[01]$")) {
                    return ApiResponse.error("성별은 0(남자) 또는 1(여자)만 입력 가능합니다.");
                }
                user.setGender(gender.equals("0") ? User.Gender.M : User.Gender.F);
            }

            // 전화번호 업데이트
            if (requestDto.getPhoneNumber() != null) {
                if (requestDto.getPhoneNumber().length() > 14) {
                    return ApiResponse.error("전화번호는 14자리 이하여야 합니다.");
                }
                if (!requestDto.getPhoneNumber().matches("^[0-9]*$")) {
                    return ApiResponse.error("전화번호는 숫자만 입력 가능합니다.");
                }
                user.setPhoneNumber(requestDto.getPhoneNumber());
            }

            userRepository.save(user);

            return ApiResponse.success("사용자 정보가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ApiResponse.error("사용자 정보 업데이트에 실패했습니다.");
        }
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

    /**
     * 로그인한 유저 혹은 파라미터로 넘긴 userId의 매칭글 리스트를 반환
     *
     * @param userIdOpt 선택적 유저 ID (없으면 JWT 기준 내 아이디)
     */
    @Operation(summary = "매칭글 조회",
            description = "로그인한 유저 또는 쿼리 파라미터로 지정한 특정 유저ID의 매칭글을 반환합니다.")
    @GetMapping("/matching-posts")
    public MypageMatchingPostsResponse getUserPosts(
            @RequestParam(name="targetUserId", required=false) Long userIdOpt,
            HttpServletRequest request) {

        // 1) 파라미터가 없으면 JWT 기준으로 내 User 가져오기
        User user;
        if (userIdOpt == null) {
            user = getCurrentUser(request);
        } else {
            user = userRepository.findById(userIdOpt.intValue())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        }

        // 2) 매칭글 조회 후 PostInfo 로 변환
        List<MypageMatchingPostsResponse.PostInfo> posts = matchingPostRepository
                .findByUserIdx(user.getUserIdx().intValue())
                .stream()
                .map(MypageMatchingPostsResponse.PostInfo::new)
                .collect(Collectors.toList());

        // 3) DTO 변환
        return new MypageMatchingPostsResponse(new MypageMatchingPostsResponse.UserInfo(user), posts);
    }

    @Operation(summary = "매칭글 is_matched 상태 토글")
    @PatchMapping("/{id}/toggle-matched")
    public ResponseEntity<ToggleMatchResponse> toggleMatched(
            @PathVariable("id") Long matchingPostIdx
    ) {
        ToggleMatchResponse response = matchingPostService.toggleMatched(matchingPostIdx);
        return ResponseEntity.ok(response);
    }
} 