package coin.spring;

import coin.spring.service.OpenAIService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner run(OpenAIService openAIService) {
		return args -> {
			try {
				// 사용자 메시지
				String userMessage = "Tell me a joke";

				// 기존 대화 기록 (초기화 예시)
				List<Map<String, String>> chatHistory = new ArrayList<>();
				Map<String, String> initialMessage = new HashMap<>();
				initialMessage.put("user", "Hello, who are you?");
				initialMessage.put("reply", "I am an AI here to assist you.");
				chatHistory.add(initialMessage);

				// OpenAI API 호출
				String result = openAIService.callOpenAIWithHistory(userMessage, chatHistory);

				// 대화 기록 업데이트
				Map<String, String> newRecord = new HashMap<>();
				newRecord.put("user", userMessage);
				newRecord.put("reply", result);
				chatHistory.add(newRecord);

				// 결과 출력
				System.out.println("API Response: " + result);

				// 전체 대화 기록 출력
				System.out.println("Chat History:");
				chatHistory.forEach(record -> {
					System.out.println("User: " + record.get("user"));
					System.out.println("AI: " + record.get("reply"));
				});
			} catch (Exception e) {
				// 예외 처리
				System.err.println("Error occurred while calling OpenAI API: " + e.getMessage());
			}
		};
	}
}


@RestController
@RequestMapping("/api/crypto")
class ProxyController {

	@GetMapping("/coin-news")
	public String getCoinNews() {
		String url = "https://openapi.naver.com/v1/search/news.json?query=코인&display=10&start=1&sort=date";

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Naver-Client-Id", "zwxqdNFydLd4lgkXXhZI");  // 네이버 클라이언트 ID
		headers.set("X-Naver-Client-Secret", "HyGhraMuAr");  // 네이버 클라이언트 Secret

		HttpEntity<String> entity = new HttpEntity<>(headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		return response.getBody();
	}




	private final RestTemplate restTemplate = new RestTemplate();

	// Upbit 암호화폐 목록 API 호출
	@GetMapping("/markets")
	public List<Map<String, Object>> getMarketList() {
		String url = "https://api.upbit.com/v1/market/all";
		List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

		// KRW 시장만 필터링
		if (response != null) {
			return response.stream()
					.filter(market -> market.get("market").toString().startsWith("KRW"))
					.toList();
		}
		return List.of();
	}

	// 선택한 암호화폐의 현재 데이터 가져오기
	@GetMapping("/ticker")
	public Map<String, Object> getTicker(@RequestParam String market) {
		String url = "https://api.upbit.com/v1/ticker?markets=" + market;
		List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

		// 데이터가 없으면 null 반환
		if (response != null && !response.isEmpty()) {
			return response.get(0);
		}
		return Map.of();
	}

	// 선택한 암호화폐의 캔들 데이터 가져오기
	@GetMapping("/candles")
	public List<Map<String, Object>> getCandles(
			@RequestParam String market,
			@RequestParam(defaultValue = "minute") String type,
			@RequestParam(defaultValue = "5") int timeframe,
			@RequestParam(defaultValue = "10") int count) {

		String baseUrl;
		if ("day".equals(type)) {
			baseUrl = "https://api.upbit.com/v1/candles/days";
		} else {
			baseUrl = "https://api.upbit.com/v1/candles/minutes/" + timeframe;
		}

		String url = baseUrl + "?market=" + market + "&count=" + count;
		List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
		return response != null ? response : List.of();
	}

	// 암호화폐 통계 데이터 가져오기
	@GetMapping("/stats")
	public Map<String, List<Map<String, Object>>> getCoinStats() {
		List<Map<String, Object>> markets = getMarketList();

		// KRW 시장의 market 값을 추출
		String marketParams = markets.stream()
				.map(market -> market.get("market").toString())
				.collect(Collectors.joining(","));

		String url = "https://api.upbit.com/v1/ticker?markets=" + marketParams;
		List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

		if (response == null || response.isEmpty()) {
			return Map.of(
					"bestVolume", List.of(),
					"bestProfit", List.of(),
					"bestTradingValue", List.of()
			);
		}

		// 데이터 정렬
		List<Map<String, Object>> bestVolume = response.stream()
				.sorted((a, b) -> Double.compare(
						Double.parseDouble(b.get("acc_trade_volume_24h").toString()),
						Double.parseDouble(a.get("acc_trade_volume_24h").toString())
				))
				.limit(5)
				.collect(Collectors.toList());

		List<Map<String, Object>> bestProfit = response.stream()
				.sorted((a, b) -> Double.compare(
						calculateProfitPercentage(b),
						calculateProfitPercentage(a)
				))
				.limit(5)
				.collect(Collectors.toList());

		List<Map<String, Object>> bestTradingValue = response.stream()
				.sorted((a, b) -> Double.compare(
						calculateTradingValue(b),
						calculateTradingValue(a)
				))
				.limit(5)
				.collect(Collectors.toList());

		return Map.of(
				"bestVolume", bestVolume,
				"bestProfit", bestProfit,
				"bestTradingValue", bestTradingValue
		);
	}

	// 수익률 계산 함수
	private double calculateProfitPercentage(Map<String, Object> coin) {
		double tradePrice = Double.parseDouble(coin.get("trade_price").toString());
		double openingPrice = Double.parseDouble(coin.get("opening_price").toString());
		return ((tradePrice - openingPrice) / openingPrice) * 100;
	}

	// 거래 금액 계산 함수
	private double calculateTradingValue(Map<String, Object> coin) {
		double tradePrice = Double.parseDouble(coin.get("trade_price").toString());
		double volume = Double.parseDouble(coin.get("acc_trade_volume_24h").toString());
		return tradePrice * volume;
	}

}