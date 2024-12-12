package coin.spring.controller;

import coin.spring.domain.dao.Member1;
import coin.spring.domain.dao.Question;
import coin.spring.domain.dto.QuestionAnswerForm;
import coin.spring.domain.dto.QuestionForm;
import coin.spring.service.MemberService;
import coin.spring.service.QuestionService;
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

@Controller // Spring MVC의 컨트롤러로 지정
@RequiredArgsConstructor // 생성자 자동 생성 및 의존성 주입을 위한 어노테이션
public class QuestionController {

    private final QuestionService questionService; // 질문 서비스 의존성 주입
    private final MemberService memberService; // 회원 서비스 의존성 주입

    // 질문 등록 폼 열기
    @GetMapping("/question/create")
    public String questionCreateForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                                     QuestionForm questionForm, Model model) {
        model.addAttribute("questionForm", new QuestionForm()); // 새로운 질문 폼 객체 추가
        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        return "user/questionForm"; // 질문 폼 뷰 반환
    }

    // 질문 등록하기
    @PostMapping("/question/create")
    public String questionCreate(@Valid @ModelAttribute("questionForm") QuestionForm questionForm,
                                 BindingResult bindingResult,
                                 @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                                 Model model) {
        // 유효성 검사 오류가 있는 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
            return "user/questionForm"; // 질문 폼으로 돌아감
        }

        // 세션에 회원 데이터가 없으면 홈으로 리다이렉트
        if (loginMember == null) {
            return "index";
        }

        // 줄바꿈을 <br>로 변환
        String content = questionForm.getContent();
        content = content.replace("\n", "<br>");  // 줄바꿈을 <br>로 변환
        questionForm.setContent(content);  // 변환된 content로 업데이트

        // 질문 객체 생성 및 설정
        Question question = new Question();
        question.setSubject(questionForm.getSubject()); // 질문 제목 설정
        question.setContent(questionForm.getContent()); // 질문 내용 설정
        question.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul"))); // 현재 시간으로 생성일 설정
        question.setAuthor(loginMember); // 작성자 설정
        questionService.create(question); // 질문 저장
        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        return "redirect:/question/list"; // 질문 목록 페이지로 리다이렉트
    }

    // 질문 목록 보기
    @GetMapping("/question/list")
    public String list(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                       Model model) {
        // 페이지네이션을 사용하여 질문 목록 조회
        Page<Question> paging = questionService.getList(page, kw);
        List<Question> questions = questionService.getQuestionsWithNotices();

        model.addAttribute("paging", paging); // 페이지 정보 추가
        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        model.addAttribute("questions", questions);
        model.addAttribute("kw", kw); // 검색 키워드 추가
        return "user/questionList"; // 질문 목록 뷰 반환
    }

    // 공지사항등록 폼 열기
    @GetMapping("/question/notice")
    public String questionNoticeCreateForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                                           QuestionForm questionForm, Model model) {
        model.addAttribute("questionForm", new QuestionForm()); // 새로운 질문 폼 객체 추가
        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        return "user/questionForm"; // 질문 폼 뷰 반환
    }

    // 공지사항 등록하기
    @PostMapping("/question/notice")
    public String questionNoticeCreate(@Valid @ModelAttribute("questionForm") QuestionForm questionForm,
                                       BindingResult bindingResult,
                                       @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                                       Model model) {
        // 유효성 검사 오류가 있는 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
            return "user/questionForm"; // 질문 폼으로 돌아감
        }

        // 세션에 회원 데이터가 없으면 홈으로 리다이렉트
        if (loginMember == null) {
            return "index";
        }

        // 줄바꿈을 <br>로 변환
        String content = questionForm.getContent();
        content = content.replace("\n", "<br>");  // 줄바꿈을 <br>로 변환
        questionForm.setContent(content);  // 변환된 content로 업데이트

        // 질문 객체 생성 및 설정
        Question question = new Question();
        question.setSubject(questionForm.getSubject()); // 질문 제목 설정
        question.setContent(questionForm.getContent()); // 질문 내용 설정
        question.setCreateDate(LocalDateTime.now()); // 현재 시간으로 생성일 설정
        question.setAuthor(loginMember); // 작성자 설정
        question.setNotice(true); // 공지사항 여부 설정
        questionService.create(question); // 질문 저장
        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        return "redirect:/question/list"; // 질문 목록 페이지로 리다이렉트
    }

    // My Page에서 내가 쓴 질문보기
    @GetMapping("/question/list/{id}")
    public String getMyQuestion(@RequestParam(value = "page", defaultValue = "0") int page, @PathVariable("id") Long memberId, @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, Model model) {

        Page<Question> paging = questionService.getMyQuestions(memberId, page);
        model.addAttribute("paging", paging); // 페이지 정보 추가
        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        return "user/questionList"; // 질문 목록으로 돌아감
    }

    // 질문 상세 보기
    @GetMapping("/question/detail/{id}")
    public String detail(@PathVariable("id") Long id,
                         @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                         QuestionAnswerForm questionAnswerForm,
                         Model model) {

        // 게시글 조회수 증가
        questionService.increaseViewCount(id);

        // 질문 조회
        Optional<Question> question = questionService.getQuestion(id);
        if (question.isPresent()) {

            // 공지사항인 경우 모든 사용자에게 접근 허용
            if (question.get().isNotice()) {
                model.addAttribute("question", question.get()); // 질문 정보 추가
                model.addAttribute("answerForm", questionAnswerForm); // 답변 폼 추가
                model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
                return "user/questionDetail"; // 질문 상세 뷰 반환
            }

            // 작성자가 아니고 관리자도 아닌 경우 접근 차단
            if (loginMember == null || !loginMember.getLoginId().equals(question.get().getAuthor().getLoginId())
                    && !loginMember.getGrade().equals("admin")) {
                model.addAttribute("errorMessage", "이 글에 접근할 권한이 없습니다.");
                return "redirect:/question/list";  // 권한이 없으면 질문 목록으로 리다이렉트
            }

            model.addAttribute("question", question.get()); // 질문 정보 추가
            model.addAttribute("answerForm", questionAnswerForm); // 답변 폼 추가
            model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
            return "user/questionDetail"; // 질문 상세 뷰 반환
        }
        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        return "user/questionList"; // 질문 목록으로 돌아감
    }

    // 수정 페이지 열기
    @GetMapping("/question/modify/{questionId}")
    public String questionModify(@PathVariable("questionId") Long questionId,
                                 @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                                 QuestionForm questionForm,
                                 Model model) {

        // 질문 조회
        Optional<Question> question = questionService.getQuestion(questionId);
        questionForm.setSubject(question.get().getSubject()); // 기존 제목 설정

        // 줄바꿈을 <br>로 변환
        String content = question.get().getContent();
        content = content.replace("<br>", "\n");  // <br>을 \n으로 변환
        questionForm.setContent(content);

        model.addAttribute("questionForm", questionForm); // 질문 폼 추가
        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        return "user/questionForm"; // 질문 수정 폼으로 이동
    }

    // 질문 수정하기
    @PostMapping("/question/modify/{questionId}")
    public String questionModify(@Valid @ModelAttribute("questionForm") QuestionForm questionForm,
                                 BindingResult bindingResult,
                                 @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                                 @PathVariable("questionId") Long questionId,
                                 Model model) {

        // 유효성 검사 오류가 있는 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
            return "user/questionForm"; // 질문 수정 폼으로 돌아감
        }

        // 세션에 회원 데이터가 없으면 홈으로 리다이렉트
        if (loginMember == null) {
            return "index";
        }

        // 줄바꿈을 <br>로 변환
        String content = questionForm.getContent();
        content = content.replace("\n", "<br>");  // 줄바꿈을 <br>로 변환
        questionForm.setContent(content);  // 변환된 content로 업데이트

        // 질문 조회
        Optional<Question> findQuestion = questionService.getQuestion(questionId);

        // 수정된 질문 객체 생성 및 설정
        Question question = new Question();
        question.setId(questionId); // ID 설정
        question.setSubject(questionForm.getSubject()); // 수정된 제목 설정
        question.setContent(questionForm.getContent()); // 수정된 내용 설정
        question.setCreateDate(findQuestion.get().getCreateDate()); // 원래 생성일 유지
        question.setModifyDate(LocalDateTime.now(ZoneId.of("Asia/Seoul"))); // 수정일 설정
        question.setAuthor(loginMember); // 작성자 설정
        questionService.create(question); // 수정된 질문 저장
        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        return String.format("redirect:/question/detail/%s", questionId); // 질문 상세 페이지로 리다이렉트
    }

    // 질문 삭제
    @GetMapping("/question/delete/{questionId}")
    public String questionDelete(@PathVariable("questionId") Long questionId,
                                 @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember) {
        // 삭제할 질문 조회
        Optional<Question> deleteQuestion = questionService.getQuestion(questionId);
        if (deleteQuestion.isPresent()) {
            questionService.delete(deleteQuestion.get()); // 질문 삭제
        }
        return "redirect:/question/list"; // 질문 목록 페이지로 리다이렉트
    }
}
