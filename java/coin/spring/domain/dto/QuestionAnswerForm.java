package coin.spring.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter  // Lombok을 사용하여 모든 필드에 대한 Getter와 Setter 메서드를 자동으로 생성
public class QuestionAnswerForm {

    // 질문에 대한 답변 내용을 위한 필드
    @NotEmpty(message = "내용은 필수 항목입니다")  // 답변 내용이 비어 있지 않은지 검사하는 유효성 검사 어노테이션
    private String content;  // 질문에 대한 답변 내용을 저장하는 필드

}
