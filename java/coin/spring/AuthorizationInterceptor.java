package coin.spring;  // 패키지 정의

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

// 사용자 권한을 확인하는 인터셉터 클래스
public class AuthorizationInterceptor implements HandlerInterceptor {

    // 요청을 처리하기 전에 실행되는 메서드
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 세션에서 사용자 등급(grade) 정보 가져오기
        Object grade = request.getSession().getAttribute("grade");

        // 접근 경로 제한: "/admin" 경로에 대한 권한 검사
        String requestURI = request.getRequestURI();  // 요청 URI를 가져옴
        if(requestURI.startsWith("/admin") && !"admin".equals(grade)) {
            // 권한이 없는 경우 접근 거부 페이지로 리다이렉트
            response.sendRedirect("/access-denied");
            return false;  // 요청을 처리하지 않음
        }

        // "/user" 경로에 대한 권한 검사
        // 일반 사용자와 관리자 모두 접근 가능
        if(requestURI.startsWith("/user") && !"user".equals(grade) && !"admin".equals(grade)) {
            // 권한이 없는 경우 접근 거부 페이지로 리다이렉트
            response.sendRedirect("/access-denied");
            return false;  // 요청을 처리하지 않음
        }

        return true;  // 권한이 확인된 경우 요청을 계속 처리
    }
}
