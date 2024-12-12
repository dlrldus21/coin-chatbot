package coin.spring.controller;

import coin.spring.domain.dao.Member1;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class CoinController {

    @GetMapping("/coin")
    public String coin(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member1 loginMember, Model model) {

        model.addAttribute("loginMember", loginMember);
        return "coin";
    }
}
