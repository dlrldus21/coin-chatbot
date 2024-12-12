package coin.spring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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";

    /**
     * OpenAI API 호출 - 대화 기록 포함
     *
     * @param userMessage 사용자 메시지
     * @param chatHistory 이전 대화 기록
     * @return OpenAI 응답 메시지
     */
    public String callOpenAIWithHistory(String userMessage, List<Map<String, String>> chatHistory) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createHeaders();
        String body = createRequestBody(userMessage, chatHistory);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
            return parseResponse(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return "OpenAI API 요청 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    /**
     * 대화 기록을 포함한 요청 본문 생성
     */
    private String createRequestBody(String userMessage, List<Map<String, String>> chatHistory) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 기존 대화 기록 추가
        for (Map<String, String> record : chatHistory) {
            messages.add(Map.of("role", "user", "content", record.get("user")));
            messages.add(Map.of("role", "assistant", "content", record.get("reply")));
        }

        // 사용자 메시지 추가
        messages.add(Map.of("role", "user", "content", userMessage));

        // JSON 문자열 생성
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", messages
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    private String parseResponse(String responseBody) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);
        return rootNode.get("choices").get(0).get("message").get("content").asText().trim();
    }
}
