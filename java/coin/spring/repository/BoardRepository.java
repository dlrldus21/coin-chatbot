package coin.spring.repository;  // 리포지토리 인터페이스를 포함하는 패키지

import coin.spring.domain.dao.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Board 엔티티에 대한 CRUD(생성, 읽기, 업데이트, 삭제) 작업을 제공하는 리포지토리 인터페이스
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 제목으로 찾기
    // 이 메서드는 제목으로 Board 엔티티를 조회하는 메서드를 추가하기 위한 자리입니다.
    // 예: List<Board> findBySubject(String subject);

    // 제목과 내용으로 찾기
    // 이 메서드는 제목과 내용으로 Board 엔티티를 조회하는 메서드를 추가하기 위한 자리입니다.
    // 예: List<Board> findBySubjectAndContent(String subject, String content);

    // findAll 메서드
    // Specification을 기반으로 필터링된 결과를 페이지 단위로 가져오는 메서드입니다.
    Page<Board> findAll(Specification<Board> spec, Pageable pageable);
    Page<Board> findByAuthor_Id(Long memberId, Pageable pageable);

    // 공지사항만 조회하는 메서드
    List<Board> findByIsNoticeTrue();

}


