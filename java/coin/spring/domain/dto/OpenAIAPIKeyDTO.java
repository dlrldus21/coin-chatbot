package coin.spring.domain.dto;

public class OpenAIAPIKeyDTO {
    private String apiKey;

    // 생성자
    public OpenAIAPIKeyDTO(String apiKey) {
        this.apiKey = apiKey;
    }

    // getter
    public String getApiKey() {
        return apiKey;
    }
    // setter
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}