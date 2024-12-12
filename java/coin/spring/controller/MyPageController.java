package coin.spring.controller;

// 필요한 도메인, DTO, 서비스 및 기타 라이브러리 임포트

import coin.spring.domain.dao.Member1;
import coin.spring.domain.dto.LoginForm;
import coin.spring.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Optional;

@Controller // Spring MVC의 컨트롤러로 지정
@RequiredArgsConstructor // 생성자 자동 생성 및 의존성 주입을 위한 어노테이션
public class MyPageController {

    private final MemberService memberService; // 회원 서비스 의존성 주입

    // 마이페이지 요청 처리
    @GetMapping("/myPage") // HTTP GET 요청을 처리
    public String myPageList(@Valid @ModelAttribute("loginForm") LoginForm loginForm, BindingResult bindingResult, @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, Model model) {

        // 로그인한 회원의 정보를 조회
        Optional<Member1> members = memberService.findOne(loginMember.getId());
        // 회원 정보가 존재하는 경우 모델에 추가
        if(members.isPresent()) {
            model.addAttribute("members", members.get());
        }

        // 로그인한 회원 정보를 모델에 추가
        model.addAttribute("loginMember", loginMember);
        return "user/myPage"; // 마이페이지 뷰로 이동
    }

}
