package dugout.DugOut.web.dto;

import dugout.DugOut.domain.User;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInfoUpdateRequestDto {
    @Pattern(regexp = "^\\d{8}$", message = "생년월일은 8자리 숫자여야 합니다.")
    private String birth;

    @Pattern(regexp = "^[01]$", message = "성별은 0(남자) 또는 1(여자)만 입력 가능합니다.")
    private String gender;

    @Size(max = 14, message = "전화번호는 14자리 이하여야 합니다.")
    @Pattern(regexp = "^[0-9]*$", message = "전화번호는 숫자만 입력 가능합니다.")
    private String phoneNumber;
} 