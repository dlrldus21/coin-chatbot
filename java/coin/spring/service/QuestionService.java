package coin.spring.service;  // 서비스 클래스를 포함하는 패키지

import coin.spring.domain.dao.Member1;
import coin.spring.domain.dao.Question;
import coin.spring.domain.dao.QuestionAnswer;
import coin.spring.repository.QuestionRepository;
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

// 질문 관련 비즈니스 로직을 처리하는 서비스 클래스
@Service
@RequiredArgsConstructor  // 생성자 주입을 위한 자동 생성자 생성
public class QuestionService {

    private final QuestionRepository questionRepository;  // QuestionRepository 인스턴스 변수

    // 질문 생성 메서드
    public void create(Question question) {
        // 주어진 질문 정보를 저장
        questionRepository.save(question);
    }

    // 전체 질문 조회 메서드
    // 페이지 번호와 검색 키워드를 입력받아 해당 페이지의 질문 목록을 반환
    // 최신순으로 데이터 확인하기
    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();  // 정렬 기준을 저장할 리스트
        sorts.add(Sort.Order.desc("createDate"));  // 생성일 기준으로 내림차순 정렬
        PageRequest pageable = PageRequest.of(page, 10, Sort.by(sorts));  // 페이지 요청 객체 생성
        // page는 조회할 페이지의 번호이고 10은 한 페이지에 보여 줄 게시물의 개수
        Specification<Question> spec = search(kw);  // 검색 조건 생성
        Page<Question> result = questionRepository.findAll(spec, pageable);  // 질문 목록 조회
        if (result.hasContent()) {
            return result;  // 결과가 있으면 반환
        } else {
            return null;  // 결과가 없으면 null 반환
            // throw new NoResultException("검색 결과가 없습니다.");  // 예외 처리 코드 (주석 처리됨)
        }
    }

    // 공지사항과 일반 게시글을 분리하여 가져오는 메서드
    public List<Question> getQuestionsWithNotices() {
        List<Question> notices = questionRepository.findByIsNoticeTrue();  // 공지사항 조회

        return notices;
    }

    // 로그인한 사용자의 질문만 가져오는 메서드 추가
    public Page<Question> getMyQuestions(Long memberId, int page) {
        List<Sort.Order> sorts = new ArrayList<>(); // 정렬 기준을 저장할 리스트
        sorts.add(Sort.Order.desc("createDate"));  // 생성일 기준으로 내림차순 정렬
        PageRequest pageable = PageRequest.of(page, 10, Sort.by(sorts));  // 페이징 처리
        Page<Question> result = questionRepository.findByAuthor_Id(memberId, pageable);  // 질문 목록 조회

        return result;
    }


    // 특정 질문 조회 메서드
    public Optional<Question> getQuestion(Long id) {
        return questionRepository.findById(id);  // 주어진 ID로 질문을 조회하여 Optional로 반환
    }

    // 질문 삭제 메서드
    public void delete(Question question) {
        questionRepository.delete(question);  // 주어진 질문 정보를 삭제
    }

    // 검색 기능 추가 메서드
    // JPA의 Specification 인터페이스를 사용하여 DB 검색을 유연하게 처리
    private Specification<Question> search(String kw) {
        return new Specification<Question>() {
            private static final long serialVersionUID = 1L;  // 직렬화 과정에서의 일관성 보장
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                // 중복제거
                query.distinct(true);  // 중복된 결과 제거
                Join<Question, Member1> m1 = q.join("author", JoinType.LEFT);  // 질문 작성자 검색
                Join<Question, QuestionAnswer> a = q.join("questionAnswerList", JoinType.LEFT);  // 답변 내용 검색
                Join<QuestionAnswer, Member1> m2 = a.join("author", JoinType.LEFT);  // 답변 작성자 검색
                // 검색 조건을 OR로 연결하여 쿼리 생성
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
    public void increaseViewCount(Long questionId) {
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if (questionOptional.isPresent()) {
            Question question = questionOptional.get();
            question.increaseViewCount();  // 조회수 증가
            questionRepository.save(question);  // 변경된 게시글 저장
        }
    }
}
