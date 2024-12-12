package coin.spring.controller;

import coin.spring.domain.dao.Member1;
import coin.spring.domain.dao.Question;
import coin.spring.domain.dao.QuestionAnswer;
import coin.spring.domain.dto.QuestionAnswerForm;
import coin.spring.service.QuestionAnswerService;
import coin.spring.service.QuestionService;
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

@Controller // Spring MVC의 컨트롤러로 지정
@RequiredArgsConstructor // 생성자 자동 생성 및 의존성 주입을 위한 어노테이션
public class QuestionAnswerController {

    private final QuestionAnswerService questionAnswerService; // 질문 답변 서비스 의존성 주입
    private final QuestionService questionService; // 질문 서비스 의존성 주입

    // 질문에 대한 답글 쓰기
    @PostMapping("/questionanswer/create/{questionid}")
    public String createAnswer(@PathVariable("questionid") Long questionid,
                               @Valid QuestionAnswerForm questionAnswerForm,
                               BindingResult bindingResult,
                               @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                               Model model) {

        // 질문 조회
        Optional<Question> question = questionService.getQuestion(questionid);

        // 유효성 검사 오류가 있는 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question.get()); // 질문 정보 추가
            model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
            return "user/questionForm"; // 질문 폼으로 돌아감
        }

        // 질문이 존재하는 경우
        if (question.isPresent()) {
            // 줄바꿈을 <br>로 변환
            String content = questionAnswerForm.getContent();
            content = content.replace("\n", "<br>");  // 줄바꿈을 <br>로 변환
            questionAnswerForm.setContent(content);  // 변환된 내용으로 설정
            // 답변 객체 생성 및 설정
            QuestionAnswer questionAnswer = new QuestionAnswer();
            questionAnswer.setContent(questionAnswerForm.getContent()); // 답변 내용 설정
            questionAnswer.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul"))); // 현재 시간으로 생성일 설정
            questionAnswer.setQuestion(question.get()); // 관련 질문 설정
            questionAnswer.setAuthor(loginMember); // 작성자 설정
            questionAnswerService.create(questionAnswer); // 답변 저장
        }

        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        return String.format("redirect:/question/detail/%s", questionid); // 질문 상세 페이지로 리다이렉트
    }

    // 답변 수정 페이지 열기
    @GetMapping("/questionanswer/modify/{answerId}")
    public String answerModify(@PathVariable("answerId") Long answerId,
                               @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                               QuestionAnswerForm questionAnswerForm,
                               Model model) {

        // 답변 조회
        Optional<QuestionAnswer> answer = questionAnswerService.getAnswer(answerId);

        // 기존 답변 내용에서 <br>을 \n으로 변환하여 수정 폼에 설정
        String content = answer.get().getContent();
        content = content.replace("<br>", "\n");  // <br>을 \n으로 변환
        questionAnswerForm.setContent(content);  // 수정 폼에 설정

        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        model.addAttribute("answerForm", questionAnswerForm); // 답변 폼 추가
        return "user/questionAnswerForm"; // 답변 수정 폼으로 이동
    }

    // 답변 수정하기
    @PostMapping("/questionanswer/modify/{answerId}")
    public String answerModify(@Valid QuestionAnswerForm questionAnswerForm,
                               BindingResult bindingResult,
                               @PathVariable("answerId") Long answerId,
                               @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                               Model model) {

        // 유효성 검사 오류가 있는 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
            return "user/questionAnswerForm"; // 수정 폼으로 돌아감
        }

        // 세션에 회원 데이터가 없으면 홈으로 이동
        if (loginMember == null) {
            return "index";
        }

        // 해당 답글 조회
        Optional<QuestionAnswer> findAnswer = questionAnswerService.getAnswer(answerId);
        // 답글에 해당하는 질문 조회
        Optional<Question> findQuestion = questionService.getQuestion(findAnswer.get().getQuestion().getId());

        // 줄바꿈을 <br>로 변환
        String content = questionAnswerForm.getContent();
        content = content.replace("\n", "<br>");  // 줄바꿈을 <br>로 변환
        questionAnswerForm.setContent(content);  // 변환된 내용으로 설정

        // 수정된 답변 객체 생성 및 설정
        QuestionAnswer questionAnswer = new QuestionAnswer();
        questionAnswer.setId(answerId); // ID 설정
        questionAnswer.setContent(questionAnswerForm.getContent()); // 수정된 내용 설정
        questionAnswer.setCreateDate(findAnswer.get().getCreateDate()); // 원래 생성일 유지
        questionAnswer.setModifyDate(LocalDateTime.now(ZoneId.of("Asia/Seoul"))); // 수정일 설정
        questionAnswer.setAuthor(loginMember); // 작성자 설정
        questionAnswer.setQuestion(findQuestion.get()); // 관련 질문 설정

        questionAnswerService.create(questionAnswer); // 수정된 답변 저장
        return String.format("redirect:/question/detail/%s", findQuestion.get().getId()); // 질문 상세 페이지로 리다이렉트
    }

    // 삭제
    @GetMapping("/questionanswer/delete/{answerId}")
    public String answer(@PathVariable("answerId") Long answerId,
                         @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember,
                         Model model) {
        // 삭제할 답변 조회
        Optional<QuestionAnswer> deleteAnswer = questionAnswerService.getAnswer(answerId);
        model.addAttribute("loginMember", loginMember); // 로그인 회원 정보 추가
        questionAnswerService.delete(deleteAnswer.get()); // 답변 삭제
        return String.format("redirect:/question/detail/%s", deleteAnswer.get().getQuestion().getId()); // 질문 상세 페이지로 리다이렉트
    }
}
