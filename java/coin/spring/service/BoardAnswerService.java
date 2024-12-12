package coin.spring.service;  // 서비스 클래스를 포함하는 패키지

import coin.spring.domain.dao.BoardAnswer;
import coin.spring.repository.BoardAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

// BoardAnswer에 대한 비즈니스 로직을 처리하는 서비스 클래스
@Service
@RequiredArgsConstructor  // 생성자 주입을 위한 자동 생성자 생성
public class BoardAnswerService {

    private final BoardAnswerRepository boardAnswerRepository;  // BoardAnswerRepository 인스턴스 변수

    // 답글을 생성하는 메서드
    public void create(BoardAnswer boardAnswer) {
        boardAnswerRepository.save(boardAnswer);  // BoardAnswer 객체를 저장
    }

    // ID로 답글을 조회하는 메서드
    public Optional<BoardAnswer> getAnswer(Long id) {
        return boardAnswerRepository.findById(id);  // 주어진 ID로 답글을 조회하고 Optional로 반환
    }

    // 답글을 삭제하는 메서드
    public void delete(BoardAnswer boardAnswer) {
        boardAnswerRepository.delete(boardAnswer);  // 주어진 답글을 삭제
    }
}
