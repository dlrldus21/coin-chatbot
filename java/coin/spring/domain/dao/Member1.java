package coin.spring.domain.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity  // 이 클래스가 JPA 엔티티임을 나타냄
@Getter @Setter  // Lombok을 사용하여 Getter와 Setter 메소드 자동 생성
public class Member1 {

    @Id  // 이 필드가 엔티티의 기본 키임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본 키 생성 전략을 IDENTITY로 설정
    @Column(name="member_id")  // DB 테이블의 열 이름을 "member_id"로 지정
    private Long id;  // 회원의 고유 식별자

    @Column(name="loginid")  // DB 테이블의 열 이름을 "loginid"로 지정
    private String loginId;  // 회원의 로그인 ID

    private String password;  // 회원의 비밀번호

    private String name;  // 회원의 이름

    private String email;  // 회원의 이메일 주소

    private String phone;  // 회원의 전화번호

    private String grade;  // 회원의 등급 (예: 일반, 관리자 등)

    // 회원이 작성한 게시글 답변 목록
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<BoardAnswer> boardAnswerList;  // 회원이 작성한 답변 리스트

    // 회원이 작성한 게시글 목록
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Board> boardList;  // 회원이 작성한 게시글 리스트

    // 회원이 작성한 문의사항 답변 목록
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<QuestionAnswer> questionAnswerList;  // 회원이 작성한 공지사항 답변 리스트

    // 회원이 작성한 문의사항 목록
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Question> questionList;  // 회원이 작성한 공지사항 리스트
}
