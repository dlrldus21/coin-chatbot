package coin.spring.domain.dao;

import coin.spring.domain.dto.OpenAIAPIKeyDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenAIDAO {

    // @Value 어노테이션을 사용하여 application.yml에서 API 키를 읽어옵니다.
    @Value("${openai.api.key}")
    private String apiKey;

    // 환경 변수에서 OpenAI API 키를 가져오는 메서드
    public OpenAIAPIKeyDTO getAPIKey() {
        // API 키가 없으면 에러 처리
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API Key가 설정되지 않았습니다!");
            return null;
        }

        // API 키를 DTO로 반환
        return new OpenAIAPIKeyDTO(apiKey);
    }
}
