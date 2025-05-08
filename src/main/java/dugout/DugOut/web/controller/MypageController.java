package dugout.DugOut.web.controller;

import dugout.DugOut.domain.MatchingPost;
import dugout.DugOut.domain.User;
import dugout.DugOut.dto.MatchingPostResponseDto;
import dugout.DugOut.dto.UserInfoUpdateRequestDto;
import dugout.DugOut.dto.UserPersonalUpdateRequestDto;
import dugout.DugOut.dto.UserTempResponseDto;
import dugout.DugOut.repository.MatchingPostRepository;
import dugout.DugOut.repository.UserRepository;
import dugout.DugOut.web.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mypage")
public class MypageController {

    private final MatchingPostRepository matchingPostRepository;
    private final UserRepository userRepository;

    public MypageController(MatchingPostRepository matchingPostRepository, UserRepository userRepository) {
        this.matchingPostRepository = matchingPostRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/myChat")
    public List<MatchingPostResponseDto> getMyChats(@RequestParam Integer userIdx) {
        List<MatchingPost> matchingPosts = matchingPostRepository.findByUserIdx(userIdx);
        return matchingPosts.stream()
                .map(MatchingPostResponseDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/myTemp")
    public UserTempResponseDto getMyTemp(@RequestParam Integer userIdx) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserTempResponseDto(user);
    }

    @PostMapping("/editInfo")
    public ApiResponse<Void> updateUserInfo(@RequestParam Integer userIdx, @RequestBody UserInfoUpdateRequestDto requestDto) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setEmail(requestDto.getEmail());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        
        userRepository.save(user);
        
        return ApiResponse.success("사용자 정보가 성공적으로 업데이트되었습니다.");
    }

    @PostMapping("/editPersonal")
    public ApiResponse<Void> updateUserPersonal(@RequestParam Integer userIdx, @RequestBody UserPersonalUpdateRequestDto requestDto) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setNickname(requestDto.getNickname());
        user.setBio(requestDto.getBio());
        user.setCheeringTeamId(requestDto.getCheeringTeamId());
        
        userRepository.save(user);
        
        return ApiResponse.success("사용자 개인정보가 성공적으로 업데이트되었습니다.");
    }

    @PostMapping("/withdraw")
    public ApiResponse<Void> withdrawUser(@RequestParam Integer userIdx) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setStatus(0);
        userRepository.save(user);
        
        return ApiResponse.success("회원탈퇴가 성공적으로 처리되었습니다.");
    }
} 