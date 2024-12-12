package coin.spring.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter  // Lombok을 사용하여 Getter와 Setter 메소드 자동 생성
public class BoardForm {

    // 게시판 글의 제목을 위한 필드
    @NotEmpty(message = "제목은 필수 항목입니다.")  // 제목이 비어 있지 않도록 유효성 검사
    private String subject;  // 게시글 제목

    // 게시판 글의 내용을 위한 필드
    @NotEmpty(message = "내용은 필수 항목입니다.")  // 내용이 비어 있지 않도록 유효성 검사
    private String content;  // 게시글 내용

}
