package coin.spring.controller;

// 필요한 도메인, DTO, 서비스 및 기타 라이브러리 임포트

import coin.spring.domain.dao.Member1;
import coin.spring.domain.dto.MemberForm;
import coin.spring.repository.MemberRepository;
import coin.spring.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller // Spring MVC의 컨트롤러로 지정
@RequiredArgsConstructor // 생성자 자동 생성 및 의존성 주입을 위한 어노테이션
public class MemberController {

    private final MemberService memberService; // 회원 서비스 의존성 주입
    private final MemberRepository memberRepository;

    // 회원 가입 폼을 보여주는 메서드
    @GetMapping("/add") // HTTP GET 요청을 처리
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm()); // 새로운 회원 폼 객체 추가
        return "user/join"; // 회원 가입 뷰로 이동
    }

    // 회원 가입 처리 메서드
    @PostMapping("/add") // HTTP POST 요청을 처리
    public String create(@Validated @ModelAttribute("memberForm") MemberForm form, BindingResult bindingResult) {
        // 유효성 검사 오류가 발생한 경우
        if (bindingResult.hasErrors()) {
            return "user/join"; // 오류가 발생한 경우 회원 가입 폼으로 돌아감
        }

        Optional<Member1> existingMember = memberRepository.findByLoginId(form.getLoginId());
        if (existingMember.isPresent()) {
            bindingResult.rejectValue("loginId", "duplicate", "이미 존재하는 회원입니다.");
            return "user/join"; // 중복 시 회원 가입 폼으로 돌아감
        }

        // 새로운 회원 객체 생성 및 정보 설정
        Member1 member1 = new Member1();
        member1.setLoginId(form.getLoginId());
        member1.setPassword(form.getPassword());
        member1.setName(form.getName());
        member1.setEmail(form.getEmail());
        member1.setPhone(form.getPhone());
        member1.setGrade("user"); // 기본 등급 설정

        memberService.join(member1); // 회원 가입 서비스 호출
        return "redirect:/";
    }

    // 회원 목록 보기
    @GetMapping("/members") // HTTP GET 요청을 처리
    public String list(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, Model model) {

        if (loginMember == null) {
            return "redirect:/"; // 홈 페이지로 리디렉션
        }

        if ("admin".equals(loginMember.getGrade())) {
            List<Member1> members = memberService.findMember(); // 회원 목록 조회
            model.addAttribute("members", members); // 모델에 회원 목록 추가
            model.addAttribute("loginMember", loginMember); // 로그인한 회원 정보 추가
            return "admin/list"; // 관리자 목록 뷰로 이동
        }
        return "redirect:/"; // 관리자 목록 뷰로 이동
    }

    // 멤버 수정 폼 보여주기
    @GetMapping("/members/{memberid}/edit") // 특정 회원 수정 요청 처리
    public String updateMemberForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, @PathVariable("memberid") Long memberid, Model model) {

        Optional<Member1> findMember = memberService.findOne(memberid); // 특정 회원 조회
        // Optional에서 회원 객체를 가져옴
        Member1 findMemberGet = findMember.get();



        // 회원이 존재하는 경우
        if (findMember.isPresent()) {
            MemberForm memberForm = new MemberForm(); // 수정할 회원 폼 객체 생성

            // 회원 정보 설정
            memberForm.setId(findMemberGet.getId());
            memberForm.setLoginId(findMemberGet.getLoginId());
            memberForm.setPassword(findMemberGet.getPassword());
            memberForm.setName(findMemberGet.getName());
            memberForm.setEmail(findMemberGet.getEmail());
            memberForm.setPhone(findMemberGet.getPhone());
            memberForm.setGrade(findMemberGet.getGrade());

            // 관리자일 경우 등급을 설정, 일반 사용자는 "user"로 고정
            if ("admin".equals(loginMember.getGrade())) {
                memberForm.setGrade(findMemberGet.getGrade());
            } else {
                memberForm.setGrade("user"); // 일반 사용자는 등급을 user로 고정
            }

            model.addAttribute("memberForm", memberForm); // 모델에 회원 수정 폼 추가
            model.addAttribute("loginMember", loginMember); // 로그인한 회원 정보 추가
            return "admin/updateMemberForm"; // 수정 폼 뷰로 이동
        }



        model.addAttribute("loginMember", loginMember); // 로그인한 회원 정보 추가
        return "admin/list"; // 회원 목록 뷰로 이동
    }

    // 멤버 수정 저장
    @PostMapping("/members/{memberid}/edit") // 특정 회원 수정 요청 처리
    public String updateMember(@Validated @ModelAttribute("memberForm") MemberForm form, BindingResult bindingResult, @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, Model model) {
        // 유효성 검사 오류가 발생한 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginMember); // 로그인한 회원 정보 추가
            return "admin/updateMemberForm"; // 수정 폼으로 돌아감
        }




        // 수정할 회원 객체 생성 및 정보 설정
        Member1 member1 = new Member1();
        member1.setId(form.getId());
        member1.setLoginId(form.getLoginId());
        member1.setPassword(form.getPassword());
        member1.setName(form.getName());
        member1.setEmail(form.getEmail());
        member1.setPhone(form.getPhone());
        member1.setGrade(form.getGrade()); // 수정된 등급 설정

        // 관리자일 경우에만 등급을 수정 가능
        if ("admin".equals(loginMember.getGrade())) {
            member1.setGrade(form.getGrade()); // 수정된 등급 설정
        } else {
            member1.setGrade("user"); // 일반 사용자는 등급을 user로 고정
        }

        memberService.save(member1); // 회원 정보 저장 서비스 호출
        model.addAttribute("loginMember", loginMember); // 로그인한 회원 정보 추가
        return "redirect:/"; // 회원 목록 페이지로 리디렉션
    }

    // 회원 삭제
    @GetMapping("/members/{memberid}/delete") // 특정 회원 삭제 요청 처리
    public String delete(@PathVariable("memberid") Long memberid) {
        Optional<Member1> findMember = memberService.findOne(memberid); // 특정 회원 조회

        // 회원이 존재하는 경우 삭제
        if (findMember.isPresent()) {
            Member1 deleteMember = findMember.get();
            memberService.delete(deleteMember); // 회원 삭제 서비스 호출
        }

        return "redirect:/members"; // 회원 목록 페이지로 리디렉션
    }

    // 관리자 트레이닝 페이지
    @GetMapping("/train") // HTTP GET 요청을 처리
    public String trainForm(Model model) {
        return "admin/train"; // 관리자 트레이닝 뷰로 이동
    }
}
