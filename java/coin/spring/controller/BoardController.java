package coin.spring.controller;

// 필요한 도메인, 서비스 및 기타 라이브러리 임포트

import coin.spring.domain.dao.Board;
import coin.spring.domain.dao.Member1;
import coin.spring.domain.dto.BoardAnswerForm;
import coin.spring.domain.dto.BoardForm;
import coin.spring.service.BoardService;
import coin.spring.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;

@Controller // Spring MVC에서 이 클래스가 웹 요청을 처리하는 컨트롤러임을 나타냄
@RequiredArgsConstructor // 의존성 주입을 위한 생성자 자동 생성
public class BoardController {

    // BoardService와 MemberService의 의존성 주입
    private final BoardService boardService;
    private final MemberService memberService;

    // 질문 등록 폼을 여는 메서드
    @GetMapping("/board/create") // HTTP GET 요청을 처리
    public String boardCreateForm(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, // 세션에서 로그인한 회원 정보
            BoardForm boardForm, // 질문 등록 폼 객체
            Model model // 뷰에 데이터를 전달하기 위한 모델
    ) {
        model.addAttribute("boardForm", new BoardForm()); // 새로운 BoardForm 객체 추가
        model.addAttribute("loginMember", loginMember); // 로그인한 회원 정보 추가
        return "user/boardForm"; // 질문 등록 폼 뷰로 이동
    }

    // 질문 등록을 처리하는 메서드
    @PostMapping("/board/create") // HTTP POST 요청을 처리
    public String boardCreate(
            @Valid @ModelAttribute("boardForm") BoardForm boardForm, // 유효성 검사와 함께 폼 데이터 바인딩
            BindingResult bindingResult, // 유효성 검사 결과
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, // 세션에서 로그인한 회원 정보
            Model model // 뷰에 데이터를 전달하기 위한 모델
    ) {
        // 유효성 검사 오류가 발생한 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginMember); // 로그인한 회원 정보 추가
            return "user/boardForm"; // 오류가 발생한 경우 질문 등록 폼으로 돌아감
        }

        // 줄바꿈을 <br>로 변환
        String content = boardForm.getContent();
        content = content.replace("\n", "<br>");  // 줄바꿈을 <br>로 변환
        boardForm.setContent(content);  // 변환된 content로 업데이트

        // 새 Board 객체 생성 및 값 설정
        Board board = new Board();
        board.setSubject(boardForm.getSubject()); // 질문 제목 설정
        board.setContent(boardForm.getContent()); // 변환된 질문 내용 설정
        board.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul"))); // 현재 시간을 생성 날짜로 설정
        board.setAuthor(loginMember); // 작성자를 로그인한 회원으로 설정

        // 질문을 데이터베이스에 저장
        boardService.create(board);

        model.addAttribute("loginMember", loginMember); // 리디렉션 시 회원 정보 추가
        return "redirect:/board/list"; // 질문 목록 페이지로 리디렉션
    }


    // 질문 목록을 보여주는 메서드
    @GetMapping("/board/list") // HTTP GET 요청을 처리
    public String list(
            @RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호 (기본값: 0)
            @RequestParam(value = "kw", defaultValue = "") String kw, // 검색 키워드 (기본값: 빈 문자열)
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, // 세션에서 로그인한 회원 정보
            Model model // 뷰에 데이터를 전달하기 위한 모델
    ) {
        // 질문 목록을 페이지네이션으로 가져옴
        Page<Board> paging = boardService.getList(page, kw);
        List<Board> boards = boardService.getBoardsWithNotices();

        model.addAttribute("paging", paging); // 페이징 정보 모델에 추가
        model.addAttribute("loginMember", loginMember); // 로그인한 회원 정보 추가
        model.addAttribute("boards", boards);
        model.addAttribute("kw", kw); // 검색 키워드 모델에 추가
        return "user/boardList"; // 질문 목록 뷰로 이동
    }

    // 공지사항등록 폼 열기
    @GetMapping("/board/notice")
    public String boardNoticeCreateForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                                        BoardForm boardForm, Model model) {
        model.addAttribute("boardForm", new BoardForm()); // 새로운 게시글 폼 객체 추가
        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        return "user/boardForm"; // 게시글 폼 뷰 반환
    }

    // 공지사항 등록하기
    @PostMapping("/board/notice")
    public String boardNoticeCreate(@Valid @ModelAttribute("boardForm") BoardForm boardForm,
                                    BindingResult bindingResult,
                                    @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                                    Model model) {
        // 유효성 검사 오류가 있는 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
            return "user/boardForm"; // 게시글 폼으로 돌아감
        }

        // 세션에 회원 데이터가 없으면 홈으로 리다이렉트
        if (loginMember == null) {
            return "index";
        }

        // 줄바꿈을 <br>로 변환
        String content = boardForm.getContent();
        content = content.replace("\n", "<br>");  // 줄바꿈을 <br>로 변환
        boardForm.setContent(content);  // 변환된 content로 업데이트

        // 게시글 객체 생성 및 설정
        Board board = new Board();
        board.setSubject(boardForm.getSubject()); // 게시글 제목 설정
        board.setContent(boardForm.getContent()); // 게시글 내용 설정
        board.setCreateDate(LocalDateTime.now()); // 현재 시간으로 생성일 설정
        board.setAuthor(loginMember); // 작성자 설정
        board.setNotice(true); // 공지사항 여부 설정
        boardService.create(board); // 게시글 저장
        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        return "redirect:/board/list"; // 게시글 목록 페이지로 리다이렉트
    }

    // My Page에서 내가 쓴 게시글 보기
    @GetMapping("/board/list/{id}")
    public String getMyBoard(@RequestParam(value = "page", defaultValue = "0") int page, @PathVariable("id") Long memberId, @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, Model model) {

        Page<Board> paging = boardService.getMyBoards(memberId, page);
        model.addAttribute("paging", paging); // 페이지 정보 추가
        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        return "user/boardList"; // 질문 목록으로 돌아감
    }


    // 특정 질문의 상세 정보를 보여주는 메서드
    @GetMapping("/board/detail/{id}") // HTTP GET 요청을 처리
    public String detail(
            @PathVariable("id") Long id, // URL에서 전달받은 질문 ID
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, // 세션에서 로그인한 회원 정보
            BoardAnswerForm boardAnswerForm, // 답변 작성 폼 객체
            Model model // 뷰에 데이터를 전달하기 위한 모델
    ) {

        // 게시글 조회수 증가
        boardService.increaseViewCount(id);

        // 질문 ID로 해당 질문을 조회
        Optional<Board> board = boardService.getBoard(id);
        if (board.isPresent()) {
            // 질문이 존재하는 경우 모델에 질문과 답변 폼 추가
            model.addAttribute("board", board.get());
            model.addAttribute("answerForm", boardAnswerForm);
            model.addAttribute("loginMember", loginMember); // 로그인한 회원 정보 추가
            return "user/boardDetail"; // 질문 상세 정보 뷰로 이동
        }

        // 질문이 존재하지 않으면 목록 페이지로 이동
        model.addAttribute("loginMember", loginMember);
        return "user/boardList"; // 질문 목록 뷰로 이동
    }

    // 질문 수정 페이지를 여는 메서드
    @GetMapping("/board/modify/{boardId}") // HTTP GET 요청을 처리
    public String boardModify(
            @PathVariable("boardId") Long boardId, // URL에서 전달받은 질문 ID
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, // 세션에서 로그인한 회원 정보
            BoardForm boardForm, // 질문 수정 폼 객체
            Model model // 뷰에 데이터를 전달하기 위한 모델
    ) {
        // 질문 ID로 해당 질문을 조회
        Optional<Board> board = boardService.getBoard(boardId);
        // 기존 질문의 제목과 내용을 수정 폼에 설정
        boardForm.setSubject(board.get().getSubject());

        // <br> 태그를 줄바꿈 문자(\n)로 변환
        String content = board.get().getContent();
        content = content.replace("<br>", "\n");  // <br>을 \n으로 변환
        boardForm.setContent(content);

        // 수정 폼과 로그인한 회원 정보를 모델에 추가
        model.addAttribute("boardForm", boardForm);
        model.addAttribute("loginMember", loginMember);
        return "user/boardForm"; // 질문 수정 폼 뷰로 이동
    }

    // 질문 수정을 처리하는 메서드
    @PostMapping("/board/modify/{boardId}") // HTTP POST 요청을 처리
    public String boardModify(
            @Valid @ModelAttribute("boardForm") BoardForm boardForm, // 유효성 검사와 함께 폼 데이터 바인딩
            BindingResult bindingResult, // 유효성 검사 결과
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, // 세션에서 로그인한 회원 정보
            @PathVariable("boardId") Long boardId, // URL에서 전달받은 질문 ID
            Model model // 뷰에 데이터를 전달하기 위한 모델
    ) {
        // 유효성 검사 오류가 발생한 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginMember); // 로그인한 회원 정보 추가
            return "user/boardForm"; // 오류가 발생한 경우 수정 폼으로 돌아감
        }

        // 세션에 회원 데이터가 없으면 홈 페이지로 이동
        if (loginMember == null) {
            return "index"; // 메인 페이지로 리디렉션
        }

        // 줄바꿈을 <br>로 변환
        String content = boardForm.getContent();
        content = content.replace("\n", "<br>");  // 줄바꿈을 <br>로 변환
        boardForm.setContent(content);  // 변환된 content로 업데이트

        // 질문 ID로 질문 조회
        Optional<Board> findBoard = boardService.getBoard(boardId);

        // 수정할 Board 객체 생성 및 값 설정
        Board board = new Board();
        board.setId(boardId); // 수정할 질문 ID 설정
        board.setSubject(boardForm.getSubject()); // 수정된 제목 설정
        board.setContent(boardForm.getContent()); // 수정된 내용 설정
        board.setCreateDate(findBoard.get().getCreateDate()); // 기존 생성 날짜 유지
        board.setModifyDate(LocalDateTime.now(ZoneId.of("Asia/Seoul"))); // 수정 날짜를 현재 시간으로 설정
        board.setAuthor(loginMember); // 작성자를 로그인한 회원으로 설정

        // 수정된 질문 저장
        boardService.create(board);
        model.addAttribute("loginMember", loginMember); // 리디렉션 시 회원 정보 추가
        return String.format("redirect:/board/detail/%s", boardId); // 수정된 질문의 상세 페이지로 리디렉션
    }

    // 질문 삭제 로직
    @GetMapping("/board/delete/{boardId}") // HTTP GET 요청을 처리
    public String boardDelete(
            @PathVariable("boardId") Long boardId, // URL에서 전달받은 질문 ID
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember // 세션에서 로그인한 회원 정보
    ) {
        // 질문 ID로 삭제할 질문 조회
        Optional<Board> deleteBoard = boardService.getBoard(boardId);
        if (deleteBoard.isPresent()) {
            boardService.delete(deleteBoard.get()); // 질문 삭제
        }
        return "redirect:/board/list"; // 질문 목록 페이지로 리디렉션
    }
}
