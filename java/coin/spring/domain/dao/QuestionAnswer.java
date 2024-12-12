package coin.spring.domain.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity  // 이 클래스가 JPA 엔티티임을 나타냄
@Getter @Setter  // Lombok을 사용하여 Getter와 Setter 메소드 자동 생성
public class QuestionAnswer {

    @Id  // 이 필드가 엔티티의 기본 키임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본 키 생성 전략을 IDENTITY로 설정
    @Column(name="questionAnswer_id")  // DB 테이블의 열 이름을 "questionAnswer_id"로 지정
    private Long id;  // 답변의 고유 식별자

    // columnDefinition = "TEXT" : 텍스트를 열 데이터로 넣을 수 있음을 의미하며, 글자 수를 제한할 수 없는 경우에 사용
    @Column(columnDefinition = "TEXT")  // 답변 내용은 TEXT 타입으로 저장
    private String content;  // 답변 내용

    private LocalDateTime createDate;  // 답변 작성 날짜

    // 해당 답변이 연관된 질문 정보
    @ManyToOne  // 다대일 관계 설정
    @JoinColumn(name="question_id")  // 외래 키가 되는 열 이름을 "question_id"로 지정
    private Question question;  // 답변이 속한 질문

    // 답변 작성자 정보
    @ManyToOne  // 다대일 관계 설정
    @JoinColumn(name="member_id")  // 외래 키가 되는 열 이름을 "member_id"로 지정
    private Member1 author;  // 답변 작성자 (로그인 사용자)

    private LocalDateTime modifyDate;  // 답변 수정 날짜
}
