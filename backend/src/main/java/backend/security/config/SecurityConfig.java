package backend.security.config;

import backend.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disabling CSRF for APIs

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                    // PUBLIC endpoints
                        .requestMatchers(
                                "/developers/signup",
                                "/developers/login"
                        ).permitAll()

                        // Only DEVELOPERS can create apps
                        .requestMatchers("/applications").hasRole("DEVELOPER")

                        // Admin-only endpoints (future)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        //Everything else requires authentication
                        .anyRequest().authenticated()
                )

                .httpBasic(httpBasic -> httpBasic.disable()) // disabling basic auth
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
