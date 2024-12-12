package coin.spring.repository;  // 리포지토리 인터페이스를 포함하는 패키지

import coin.spring.domain.dao.Member1;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Member 엔티티에 대한 CRUD(생성, 읽기, 업데이트, 삭제) 작업을 제공하는 리포지토리 인터페이스
public interface MemberRepository extends JpaRepository<Member1, Long> {

    // 로그인 ID로 회원을 찾는 메서드
    Optional<Member1> findByLoginId(String loginId);
}
