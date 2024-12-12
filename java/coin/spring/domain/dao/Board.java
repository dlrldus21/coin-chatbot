package coin.spring.domain.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity  // 이 클래스가 JPA 엔티티임을 나타냄
@Getter @Setter  // Lombok을 사용하여 Getter와 Setter 메소드 자동 생성
public class Board {

    @Id  // 이 필드가 엔티티의 기본 키임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본 키 생성 전략을 IDENTITY로 설정
    @Column(name="board_id")  // DB 테이블의 열 이름을 "board_id"로 지정
    private Long id;  // 게시글의 고유 식별자

    @Column(length = 200)  // 이 필드의 길이를 200자로 제한
    private String subject;  // 게시글 제목

    // columnDefinition = "TEXT" : 글자 수 제한 없이 긴 텍스트를 저장할 수 있음을 나타냄
    @Column(columnDefinition = "TEXT")  // DB에서 TEXT 타입으로 저장
    private String content;  // 게시글 내용

    private LocalDateTime createDate;  // 게시글 작성일시

    // 공지사항 여부를 나타내는 필드
    private boolean isNotice = false;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)  // 일대다 관계 설정
    private List<BoardAnswer> boardAnswerList;  // 게시글에 달린 답변 리스트
    // 하나의 게시글에 여러 개의 답변이 달릴 수 있음.
    // cascade = CascadeType.REMOVE : 게시글 삭제 시, 해당 게시글에 달린 모든 답변도 함께 삭제됨.

    @ManyToOne  // 다대일 관계 설정
    @JoinColumn(name="member_id")  // 이 관계에서 사용할 외래 키 열 이름을 "member_id"로 지정
    private Member1 author;  // 게시글 작성자 (로그인한 사용자)

    private LocalDateTime modifyDate;  // 게시글 수정일시

    // 조회수를 저장하는 필드 추가
    private int viewCount = 0;  // 기본값은 0으로 설정

    // 조회수 증가 메서드 추가
    public void increaseViewCount() {
        this.viewCount++;  // 조회수 증가
    }
}
