package backend.security.filter;

import backend.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // 1. Checking if header exist
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extracting the token
        String token = authHeader.substring(7);

        // 3. Validating the token
        if (!jwtService.isTokenValid(token)){
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Extracting developerId
        UUID developerId = jwtService.extractDeveloperId(token);

        // 5. Extracting role
        String role = jwtService.extractRole(token);


        // 6. Creating authentication object
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        developerId,
                        null,
                        List.of(new SimpleGrantedAuthority(role))
                );

        // 7. Setting authentication in context
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // continue request
        filterChain.doFilter(request, response);
    }

}
