package coin.spring.controller;

import coin.spring.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/openai")
public class OpenAIController {

    @Autowired
    private OpenAIService openAIService;

    // 대화 기록 관리
    private final List<Map<String, String>> chatHistory = new ArrayList<>();

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");

        // OpenAI API 호출
        String reply = openAIService.callOpenAIWithHistory(prompt, chatHistory);

        // 대화 기록 업데이트
        Map<String, String> messageRecord = new HashMap<>();
        messageRecord.put("user", prompt);
        messageRecord.put("reply", reply);
        chatHistory.add(messageRecord);

        // 응답 반환
        Map<String, Object> response = new HashMap<>();
        response.put("reply", reply);
        response.put("history", chatHistory);
        return response;
    }
}
