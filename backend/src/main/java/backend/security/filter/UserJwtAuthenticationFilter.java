package backend.security.filter;

import backend.application.entity.Application;
import backend.application.repository.ApplicationRepository;
import backend.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ApplicationRepository applicationRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. Check header FIRST
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract token AFTER validation
        String token = authHeader.substring(7);

        // 3. Validate token
        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Get API key
        String apiKey = request.getHeader("x-api-key");

        if (apiKey == null || apiKey.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Find application
        Application app = applicationRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new RuntimeException("Invalid API Key"));

        // 6. Extract from token
        UUID userId = jwtService.extractUserId(token);
        UUID tokenAppId = jwtService.extractApplicationId(token);

        // CRITICAL SECURITY CHECK
        if (!app.getId().equals(tokenAppId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Cross-application access denied");
            return;
        }

        // 7. Extract role
        String role = jwtService.extractRole(token);

        // 8. Set authentication
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority(role))
                );

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}