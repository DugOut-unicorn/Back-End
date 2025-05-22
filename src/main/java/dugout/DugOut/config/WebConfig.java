package dugout.DugOut.config;

import dugout.DugOut.web.interceptor.JwtAuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/mypage/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 엔드포인트에 대해 CORS 설정 적용
                .allowedOrigins("https://*.dug-out.store/", "http://localhost:3000")  // 프론트엔드 개발 서버 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용할 HTTP 메서드
                .allowedHeaders("*")  // 모든 헤더 허용
                .exposedHeaders("Authorization")  // JWT 토큰을 담은 Authorization 헤더 노출
                .allowCredentials(true)  // 쿠키, 인증 헤더 등을 포함한 요청 허용
                .maxAge(3600);  // preflight 요청 결과를 캐시하는 시간 (초)
    }
} 