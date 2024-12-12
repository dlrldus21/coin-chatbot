package coin.spring.repository;  // 리포지토리 인터페이스를 포함하는 패키지

import coin.spring.domain.dao.BoardAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

// BoardAnswer 엔티티에 대한 CRUD(생성, 읽기, 업데이트, 삭제) 작업을 제공하는 리포지토리 인터페이스
public interface BoardAnswerRepository extends JpaRepository<BoardAnswer, Long> {
    // JpaRepository를 상속받아 기본적인 CRUD 메서드를 자동으로 사용할 수 있습니다.
    // BoardAnswer 엔티티를 다루며, id의 타입은 Long입니다.
}
