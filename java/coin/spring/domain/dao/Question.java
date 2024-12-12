package coin.spring.domain.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity  // 이 클래스가 JPA 엔티티임을 나타냄
@Getter @Setter  // Lombok을 사용하여 Getter와 Setter 메소드 자동 생성
public class Question {

    @Id  // 이 필드가 엔티티의 기본 키임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본 키 생성 전략을 IDENTITY로 설정
    @Column(name="question_id")  // DB 테이블의 열 이름을 "question_id"로 지정
    private Long id;  // 질문의 고유 식별자

    @Column(length = 200)  // 질문 제목의 최대 길이를 200자로 제한
    private String subject;  // 질문 제목

    // columnDefinition = "TEXT" : 텍스트를 열 데이터로 넣을 수 있음을 의미하며, 글자 수를 제한할 수 없는 경우에 사용
    @Column(columnDefinition = "TEXT")  // 질문 내용은 TEXT 타입으로 저장
    private String content;  // 질문 내용

    private LocalDateTime createDate;  // 질문 작성 날짜

    // 공지사항 여부를 나타내는 필드
    private boolean isNotice = false;

    // 해당 질문에 대한 답변 목록
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)  // 질문과 답변 간의 관계 설정
    private List<QuestionAnswer> questionAnswerList;  // 여러 답변을 포함할 수 있는 리스트
    // cascade = CascadeType.REMOVE : 질문을 삭제하면 그에 달린 답변들도 모두 삭제된다.

    // 질문 작성자 정보
    @ManyToOne  // 다대일 관계 설정
    @JoinColumn(name="member_id")  // 외래 키가 되는 열 이름을 "member_id"로 지정
    private Member1 author;  // 질문 작성자 (로그인 사용자)

    private LocalDateTime modifyDate;  // 질문 수정 날짜

    // 조회수를 저장하는 필드 추가
    private int viewCount = 0;  // 기본값은 0으로 설정

    // 조회수 증가 메서드 추가
    public void increaseViewCount() {
        this.viewCount++;  // 조회수 증가
    }
}
