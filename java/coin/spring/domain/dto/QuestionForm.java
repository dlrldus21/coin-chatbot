package coin.spring.domain.dto;  // DTO(Data Transfer Object)를 포함하는 패키지

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter  // Lombok을 사용하여 모든 필드에 대한 Getter와 Setter 메서드를 자동 생성
public class QuestionForm {

    // 질문의 제목을 저장하기 위한 필드
    @NotEmpty(message = "제목은 필수 항목입니다.")  // 제목 필드가 비어 있지 않은지 검사하는 유효성 검사 어노테이션
    private String subject;  // 질문 제목을 저장하는 필드

    // 질문의 내용을 저장하기 위한 필드
    @NotEmpty(message = "내용은 필수 항목입니다.")  // 내용 필드가 비어 있지 않은지 검사하는 유효성 검사 어노테이션
    private String content;  // 질문 내용을 저장하는 필드

}
