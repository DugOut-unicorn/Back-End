package dugout.DugOut.web.interceptor;

import dugout.DugOut.service.JwtService;
import dugout.DugOut.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String token = extractToken(request);
            log.info("[JWT Interceptor] Received token: {}", token);
            
            if (token == null) {
                log.error("[JWT Interceptor] Token is null");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            if (!jwtService.validateToken(token)) {
                log.error("[JWT Interceptor] Token validation failed");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            String email = jwtService.getEmailFromToken(token);
            log.info("[JWT Interceptor] Extracted email: {}", email);

            userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("[JWT Interceptor] User not found for email: {}", email);
                        return new RuntimeException("User not found");
                    });

            return true;
        } catch (Exception e) {
            log.error("[JWT Interceptor] Error processing token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }
} 