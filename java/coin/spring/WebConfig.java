package coin.spring;  // 패키지 정의

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

// Spring MVC 관련 설정을 위한 클래스
@Configuration  // 이 클래스가 Spring의 설정 클래스임을 나타냄
public class WebConfig implements WebMvcConfigurer {

    // 인터셉터를 등록하는 메서드
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // AuthorizationInterceptor를 등록하고 특정 경로에 대해 적용
        registry.addInterceptor(new AuthorizationInterceptor())
                .addPathPatterns("/admin/**", "/user/**");  // /admin/** 및 /user/** 경로에 인터셉터 적용
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080") // 클라이언트 출처
                .allowedOrigins("http://3.36.0.179:8080/")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
