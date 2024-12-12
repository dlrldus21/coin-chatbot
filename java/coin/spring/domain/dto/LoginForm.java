package coin.spring.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {

    private Long id;  // 사용자의 고유 ID (로그인에는 사용되지 않지만, 필요 시 사용할 수 있음)

    // 로그인 ID를 위한 필드
    @NotEmpty(message = "아이디는 필수입니다.")  // 아이디가 비어 있지 않도록 유효성 검사
    @Size(max=200)  // 아이디의 최대 길이를 200자로 제한
    private String loginId;  // 사용자 아이디

    // 비밀번호를 위한 필드
    @NotEmpty(message = "비밀번호는 필수입니다.")  // 비밀번호가 비어 있지 않도록 유효성 검사
    private String password;  // 사용자 비밀번호
}
