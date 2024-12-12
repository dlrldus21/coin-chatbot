package coin.spring.controller;

// 필요한 도메인, 서비스, 유효성 검사 및 기타 라이브러리 임포트

import coin.spring.domain.dao.Board;
import coin.spring.domain.dao.BoardAnswer;
import coin.spring.domain.dao.Member1;
import coin.spring.domain.dto.BoardAnswerForm;
import coin.spring.service.BoardAnswerService;
import coin.spring.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Controller // 이 클래스가 Spring의 컨트롤러임을 나타냄
@RequiredArgsConstructor // 의존성 주입을 위한 생성자 자동    생성
public class BoardAnswerController {

    // BoardAnswerService와 BoardService의 의존성 주입
    private final BoardAnswerService boardAnswerService;
    private final BoardService boardService;

    // 질문에 대한 답글을 작성하는 메서드
    @PostMapping("/boardanswer/create/{boardid}") // HTTP POST 요청을 처리
    public String createAnswer(
            @PathVariable("boardid") Long boardid, // URL에서 전달받은 board ID
            @Valid BoardAnswerForm boardAnswerForm, // 클라이언트로부터 받은 답변 데이터 폼
            BindingResult bindingResult, // 유효성 검사 결과를 담고 있는 객체
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, // 세션에서 로그인한 회원 정보
            Model model // 뷰에 전달할 데이터를 담기 위한 모델
    ) {
        // boardid로 해당 보드를 조회
        Optional<Board> board = boardService.getBoard(boardid);

        // 유효성 검사 오류가 발생한 경우
        if (bindingResult.hasErrors()) {
            // 오류가 발생한 경우, 현재 보드와 로그인한 회원 정보를 모델에 추가
            model.addAttribute("board", board.get());
            model.addAttribute("loginMember", loginMember);
            return "user/boardForm"; // 답글 작성 폼으로 이동
        }

        // 보드가 존재할 경우
        if (board.isPresent()) {
            // 줄바꿈을 <br>로 변환
            String content = boardAnswerForm.getContent();
            content = content.replace("\n", "<br>");  // 줄바꿈을 <br>로 변환
            boardAnswerForm.setContent(content);  // 변환된 내용으로 설정
            // 새로운 BoardAnswer 객체 생성
            BoardAnswer boardAnswer = new BoardAnswer();
            boardAnswer.setContent(boardAnswerForm.getContent()); // 답변 내용 설정
            boardAnswer.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul"))); // 현재 시간을 생성 날짜로 설정
            boardAnswer.setBoard(board.get()); // 현재 보드와 연결
            boardAnswer.setAuthor(loginMember); // 로그인한 회원을 작성자로 설정
            boardAnswerService.create(boardAnswer); // 답변 저장
        }

        // 리디렉션 시 로그인한 회원 정보를 모델에 추가
        model.addAttribute("loginMember", loginMember);
        return String.format("redirect:/board/detail/%s", boardid); // 보드 상세 페이지로 리디렉션
    }

    // 답변 수정 페이지를 여는 메서드
    @GetMapping("/boardanswer/modify/{answerId}") // HTTP GET 요청을 처리
    public String answerModify(
            @PathVariable("answerId") Long answerId, // URL에서 전달받은 답변 ID
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, // 로그인한 회원 정보
            BoardAnswerForm boardAnswerForm, // 답변 수정 폼 객체
            Model model // 뷰에 전달할 데이터를 담기 위한 모델
    ) {
        // 답변 ID로 해당 답변을 조회
        Optional<BoardAnswer> boardAnswer = boardAnswerService.getAnswer(answerId);

        // 기존 답변 내용에서 <br>을 \n으로 변환하여 수정 폼에 설정
        String content = boardAnswer.get().getContent();
        content = content.replace("<br>", "\n");  // <br>을 \n으로 변환
        boardAnswerForm.setContent(content);  // 수정 폼에 설정


        // 로그인한 회원과 수정 폼 데이터를 모델에 추가
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("boardAnswerForm", boardAnswerForm);
        return "user/boardAnswerForm"; // 답변 수정 폼 뷰로 이동
    }

    // 답변을 수정하는 메서드
    @PostMapping("/boardanswer/modify/{answerId}") // HTTP POST 요청을 처리
    public String answerModify(
            @Valid BoardAnswerForm boardAnswerForm, // 수정된 답변 내용
            BindingResult bindingResult, // 유효성 검사 결과
            @PathVariable("answerId") Long answerId, // URL에서 전달받은 답변 ID
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, // 로그인한 회원 정보
            Model model // 뷰에 전달할 데이터를 담기 위한 모델
    ) {
        // 유효성 검사 오류가 발생한 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginMember); // 로그인한 회원 정보 추가
            return "user/boardAnswerForm"; // 수정 폼으로 돌아감
        }

        // 세션에 회원 데이터가 없으면 홈 페이지로 이동
        if (loginMember == null) {
            return "index"; // 메인 페이지로 리디렉션
        }

        // 답변 ID로 답변 조회
        Optional<BoardAnswer> findAnswer = boardAnswerService.getAnswer(answerId);
        // 답변에 해당하는 질문 조회
        Optional<Board> findBoard = boardService.getBoard(findAnswer.get().getBoard().getId());

        // 줄바꿈을 <br>로 변환
        String content = boardAnswerForm.getContent();
        content = content.replace("\n", "<br>");  // 줄바꿈을 <br>로 변환
        boardAnswerForm.setContent(content);  // 변환된 내용으로 설정

        // 새로운 BoardAnswer 객체 생성 및 값 설정
        BoardAnswer boardAnswer = new BoardAnswer();
        boardAnswer.setId(answerId); // 수정할 답변 ID 설정
        boardAnswer.setContent(boardAnswerForm.getContent()); // 수정된 내용 설정
        boardAnswer.setCreateDate(findAnswer.get().getCreateDate()); // 기존 생성 날짜 유지
        boardAnswer.setModifyDate(LocalDateTime.now(ZoneId.of("Asia/Seoul"))); // 수정 날짜를 현재 시간으로 설정
        boardAnswer.setAuthor(loginMember); // 작성자를 로그인한 회원으로 설정
        boardAnswer.setBoard(findBoard.get()); // 보드와 연결

        // 수정된 답변 저장
        boardAnswerService.create(boardAnswer);
        return String.format("redirect:/board/detail/%s", findBoard.get().getId()); // 보드 상세 페이지로 리디렉션
    }

    // 답변 삭제 메서드
    @GetMapping("/boardanswer/delete/{answerId}") // HTTP GET 요청을 처리
    public String answer(
            @PathVariable("answerId") Long answerId, // URL에서 전달받은 답변 ID
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, // 로그인한 회원 정보
            Model model // 뷰에 전달할 데이터를 담기 위한 모델
    ) {
        // 답변 ID로 삭제할 답변 조회
        Optional<BoardAnswer> deleteAnswer = boardAnswerService.getAnswer(answerId);
        model.addAttribute("loginMember", loginMember); // 로그인한 회원 정보 추가
        boardAnswerService.delete(deleteAnswer.get()); // 해당 답변 삭제
        return String.format("redirect:/board/detail/%s", deleteAnswer.get().getBoard().getId()); // 보드 상세 페이지로 리디렉션
    }
}
