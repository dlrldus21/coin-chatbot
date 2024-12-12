package coin.spring.repository;  // 리포지토리 인터페이스를 포함하는 패키지

import coin.spring.domain.dao.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

// QuestionAnswer 엔티티에 대한 CRUD(생성, 읽기, 업데이트, 삭제) 작업을 제공하는 리포지토리 인터페이스
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {
    // 기본적인 CRUD 작업은 JpaRepository에서 제공하므로, 추가적인 메서드가 필요한 경우 이곳에 정의할 수 있음
}
