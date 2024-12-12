package coin.spring.service;  // 서비스 클래스를 포함하는 패키지

import coin.spring.domain.dao.Board;
import coin.spring.domain.dao.BoardAnswer;
import coin.spring.domain.dao.Member1;
import coin.spring.repository.BoardRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Board 관련 비즈니스 로직을 처리하는 서비스 클래스
@Service
@RequiredArgsConstructor  // 생성자 주입을 위한 자동 생성자 생성
public class BoardService {

    private final BoardRepository boardRepository;  // BoardRepository 인스턴스 변수

    // 질문을 생성하고 저장하는 메서드
    public void create(Board board) {
        boardRepository.save(board);  // Board 객체를 저장
    }

    // 전체 질문을 조회하고 페이지네이션을 적용하는 메서드
    //위의 질문 몰록을 조회하는 부분을 정수타입의 페이지 번호를 입력받아 해당 페이지의 page객체를 리턴하도록 변경한다
    //최신순으로 데이터 확인하기
    public Page<Board> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();  // 정렬 조건을 저장할 리스트 생성
        sorts.add(Sort.Order.desc("createDate"));  // 생성일 기준으로 내림차순 정렬 추가
        PageRequest pageable = PageRequest.of(page, 10, Sort.by(sorts));  // 페이지 요청 생성 (페이지 번호, 페이지 크기, 정렬)

        // 검색 조건에 따른 Specification 생성
        Specification<Board> spec = search(kw);
        // 주어진 Specification과 Pageable을 사용하여 Board 목록 조회
        Page<Board> result = boardRepository.findAll(spec, pageable);

        // 조회 결과가 존재하는지 확인
        if (result.hasContent()) {
            return result;  // 결과가 있으면 반환
        } else {
            return null;  // 결과가 없으면 null 반환
            // throw new NoResultException("검색 결과가 없습니다."); // 예외 처리 (주석 처리됨)
        }
    }

    // 공지사항과 일반 게시글을 분리하여 가져오는 메서드
    public List<Board> getBoardsWithNotices() {
        List<Board> notices = boardRepository.findByIsNoticeTrue();  // 공지사항 조회

        return notices;
    }

    // 로그인한 사용자의 질문만 가져오는 메서드 추가
    public Page<Board> getMyBoards(Long memberId, int page) {
        List<Sort.Order> sorts = new ArrayList<>(); // 정렬 기준을 저장할 리스트
        sorts.add(Sort.Order.desc("createDate"));  // 생성일 기준으로 내림차순 정렬
        PageRequest pageable = PageRequest.of(page, 10, Sort.by(sorts));  // 페이징 처리
        Page<Board> result = boardRepository.findByAuthor_Id(memberId, pageable);  // 질문 목록 조회

        return result;
    }


    // 특정 ID로 질문을 조회하는 메서드
    public Optional<Board> getBoard(Long id) {
        return boardRepository.findById(id);  // 주어진 ID로 Board 객체 조회
    }

    // 질문을 삭제하는 메서드
    public void delete(Board board) {
        boardRepository.delete(board);  // 주어진 Board 객체를 삭제
    }

    // 검색 기능을 위한 Specification 생성 메서드
    private Specification<Board> search(String kw) {
        return new Specification<Board>() {

            private static final long serialVersionUID = 1L;  // 직렬화 과정에서의 일관성 보장

            @Override
            public Predicate toPredicate(Root<Board> q, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                query.distinct(true);  // 중복 제거 설정
                Join<Board, Member1> m1 = q.join("author", JoinType.LEFT);  // 질문 작성자와의 조인
                Join<Board, BoardAnswer> a = q.join("boardAnswerList", JoinType.LEFT);  // 답변 리스트와의 조인
                Join<BoardAnswer, Member1> m2 = a.join("author", JoinType.LEFT);  // 답변 작성자와의 조인

                // 여러 조건을 OR로 결합하여 Predicate 반환
                return criteriaBuilder.or(
                        criteriaBuilder.like(q.get("subject"), "%" + kw + "%"),  // 제목 검색
                        criteriaBuilder.like(q.get("content"), "%" + kw + "%"),  // 질문 내용 검색
                        criteriaBuilder.like(m1.get("name"), "%" + kw + "%"),  // 질문 작성자 이름 검색
                        criteriaBuilder.like(m1.get("loginId"), "%" + kw + "%"),  // 질문 작성자 아이디 검색
                        criteriaBuilder.like(a.get("content"), "%" + kw + "%"),  // 답변 내용 검색
                        criteriaBuilder.like(m2.get("name"), "%" + kw + "%")  // 답변 작성자 이름 검색
                );
            }
        };
    }
    // 특정 게시글 조회수 증가 메서드
    public void increaseViewCount(Long boardId) {
        Optional<Board> boardOptional = boardRepository.findById(boardId);
        if (boardOptional.isPresent()) {
            Board board = boardOptional.get();
            board.increaseViewCount();  // 조회수 증가
            boardRepository.save(board);  // 변경된 게시글 저장
        }
    }
}
