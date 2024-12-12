package coin.spring.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm {

    private Long id;  // 사용자의 고유 ID (회원 가입 시 사용되지 않지만, 필요 시 사용할 수 있음)

    // 로그인 아이디를 위한 필드
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영어와 숫자만 포함되어야 합니다.")
    private String loginId;  // 사용자 아이디

    // 비밀번호를 위한 필드
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{6,}$",
            message = "비밀번호는 영어와 숫자를 포함한 6자 이상의 비밀번호여야 합니다."
    )  // 정규 표현식으로 비밀번호 검증
    private String password;  // 사용자 비밀번호

    // 사용자 이름을 위한 필드
    @NotEmpty(message = "이름은 필수입니다.")  // 이름이 비어 있지 않도록 유효성 검사
    private String name;  // 사용자 이름

    // 이메일을 위한 필드
    @NotEmpty(message = "이메일은 필수입니다.")
    private String email;  // 사용자 이메일 주소

    // 전화번호를 위한 필드
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$",
            message = "전화번호는 '000-0000-0000' 형식으로 입력해주세요.")
    private String phone;  // 사용자 전화번호 (숫자형)

    // 사용자 등급을 위한 필드
    private String grade;  // 사용자 등급 (예: 일반, 관리자 등)
}
