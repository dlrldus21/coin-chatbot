package coin.spring.controller;

// 필요한 도메인, DTO, 서비스 및 기타 라이브러리 임포트

import coin.spring.domain.dao.Member1;
import coin.spring.domain.dto.LoginForm;
import coin.spring.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller // Spring MVC의 컨트롤러로 지정
@RequiredArgsConstructor // 생성자 자동 생성 및 의존성 주입을 위한 어노테이션
public class LoginController {

    private final LoginService loginService; // 로그인 서비스 의존성 주입

    // 로그인 폼을 보여주는 메서드
    @GetMapping("/login") // HTTP GET 요청을 처리
    public String loginForm(Model model, HttpServletRequest request) {
        // 로그인 전에 접근한 URL을 세션에 저장
        String referer = request.getHeader("Referer");
        request.getSession().setAttribute("prevPage", referer);

        // 새로운 로그인 폼 객체를 모델에 추가
        model.addAttribute("loginForm", new LoginForm());
        return "user/login"; // 로그인 뷰로 이동
    }

    // 로그인 처리 메서드
    @PostMapping("/login") // HTTP POST 요청을 처리
    public String login(
            @Valid @ModelAttribute("loginForm") LoginForm loginForm, // 유효성 검사를 위한 로그인 폼
            BindingResult bindingResult, // 유효성 검사 결과
            HttpServletRequest request // HTTP 요청
    ) {
        // 유효성 검사 오류가 발생한 경우
        if (bindingResult.hasErrors()) {
            return "user/login"; // 오류가 발생한 경우 로그인 폼으로 돌아감
        }

        // 로그인 서비스 호출하여 회원 정보 조회
        Member1 loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        // 로그인 실패 시 오류 메시지 추가
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 올바르지 않습니다."); // 오류 메시지 추가
            return "user/login"; // 로그인 폼으로 돌아감
        }

        // 로그인 성공 시 세션 생성
        HttpSession session = request.getSession();
        // 세션에 로그인한 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember); // 세션에 로그인 회원 정보 저장

        String prevPage = (String) session.getAttribute("prevPage");
        if (prevPage != null && !prevPage.isEmpty()) {
            // 로그인 페이지나 회원가입 페이지인 경우 홈 페이지로 리디렉션
            if (prevPage.contains("/login") || prevPage.contains("/add")) {
                return "redirect:/"; // 홈 페이지로 리디렉션
            }
            return "redirect:" + prevPage; // 유효한 직전 페이지로 리디렉션
        }

        return "redirect:/"; // 홈 페이지로 리디렉션
    }

    // 로그아웃 기능
    @PostMapping("/logout") // HTTP POST 요청을 처리
    public String logout(HttpServletRequest request) {
        // 세션을 가져오되, 존재하지 않으면 null 반환
        HttpSession session = request.getSession(false);

        // 세션이 존재하면 세션 무효화
        if (session != null) {
            session.invalidate(); // 세션 제거
        }
        return "redirect:/"; // 홈 페이지로 리디렉션
    }

    // 마이페이지 (현재 주석 처리됨)
    // @GetMapping("/myPage")
    // public String myPageList(HttpServletRequest request, Model model) {
    //     HttpSession session = request.getSession(false);
    //     if (session != null) {
    //         Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
    //         if (loginMember != null) {
    //             model.addAttribute("loginMember", loginMember);
    //         }
    //     }
    //     return "user/myPage";
    // }
}
