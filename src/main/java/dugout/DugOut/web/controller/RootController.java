package dugout.DugOut.web.controller;

import dugout.DugOut.domain.User;
import dugout.DugOut.domain.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class RootController {

    private final UserRepository userRepository;

    public RootController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/health")
    public String healthCheck(){
        return "Health Check complete!";
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/test-data")
    public String addTestData() {
        User user = new User();
        user.setName("테스트");
        user.setNickname("테스트닉네임3");
        user.setCheeringTeamId(1);
        user.setBio("테스트3 바이오");
        user.setBirth(LocalDate.of(1990, 1, 1));
        user.setGender(User.Gender.M);
        user.setEmail("test3@test.com");
        user.setPhoneNumber("010-1234-4678");
        user.setStatus(0);
        
        userRepository.save(user);
        return "테스트 데이터 추가 완료";
    }
}
