package coin.spring.controller;

import coin.spring.domain.dao.Member1;
import coin.spring.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private OpenAIService openAIService;

    // 대화 기록을 저장할 리스트
    private final List<Map<String, String>> chatHistory = new ArrayList<>();

    /**
     * 사용자로부터 메시지를 받아 OpenAI API를 호출하고 응답 반환
     *
     * @param request 사용자 요청 메시지
     * @return AI 응답 메시지와 대화 기록
     */
    @PostMapping
    @ResponseBody
    public Map<String, Object> chat(@RequestBody Map<String, String> request) {
        // 요청에서 플래그 추출
        boolean reset = Boolean.parseBoolean(request.getOrDefault("reset", "false"));

        // 초기화 플래그가 true인 경우 대화 기록 초기화
        if (reset) {
            chatHistory.clear();  // 대화 기록 초기화
            Map<String, Object> result = new HashMap<>();
            result.put("reply", "대화 기록이 초기화되었습니다.");
            result.put("history", chatHistory);
            return result;  // 초기화된 대화 기록 반환
        }

        // 사용자 메시지 추출
        String userMessage = request.get("message");

        // OpenAI API 호출: 대화 기록을 포함하여 호출
        String response = openAIService.callOpenAIWithHistory(userMessage, chatHistory);

        // 대화 기록 추가
        Map<String, String> messageRecord = new HashMap<>();
        messageRecord.put("user", userMessage);
        messageRecord.put("reply", response);
        chatHistory.add(messageRecord);

        // 응답과 전체 대화 기록 반환
        Map<String, Object> result = new HashMap<>();
        result.put("reply", response);
        result.put("history", chatHistory);
        return result;  // JSON 응답 반환
    }

    /**
     * index1 페이지로 매핑
     *
     * @return index1.html 페이지
     */
    @GetMapping("/index1")
    public String index1(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false)Member1 loginMember, Model model) {

        model.addAttribute("loginMember", loginMember);
        return "index1";  // index1.html 파일이 src/main/resources/templates/ 폴더에 있어야 합니다.
    }
}
