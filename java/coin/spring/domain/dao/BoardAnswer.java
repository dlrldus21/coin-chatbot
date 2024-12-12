package coin.spring.domain.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity  // 이 클래스가 JPA 엔티티임을 나타냄
@Getter @Setter  // Lombok을 사용하여 Getter와 Setter 메소드 자동 생성
public class BoardAnswer {

    @Id  // 이 필드가 엔티티의 기본 키임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본 키 생성 전략을 IDENTITY로 설정
    @Column(name="boardAnswer_id")  // DB 테이블의 열 이름을 "boardAnswer_id"로 지정
    private Long id;  // 답변의 고유 식별자

    @Column(columnDefinition = "TEXT")  // DB에서 TEXT 타입으로 저장
    private String content;  // 답변 내용

    private LocalDateTime createDate;  // 답변 작성일시

    @ManyToOne  // 다대일 관계 설정
    @JoinColumn(name="board_id")  // 이 관계에서 사용할 외래 키 열 이름을 "board_id"로 지정
    private Board board;  // 이 답변이 속한 게시글 정보

    @ManyToOne  // 다대일 관계 설정
    @JoinColumn(name="member_id")  // 이 관계에서 사용할 외래 키 열 이름을 "member_id"로 지정
    private Member1 author;  // 답변 작성자 (로그인한 사용자)

    private LocalDateTime modifyDate;  // 답변 수정일시
}
