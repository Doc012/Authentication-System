package backend.auth.security.config;

import backend.auth.security.filter.JwtAuthenticationFilter;
import backend.auth.security.filter.UserJwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final UserJwtAuthenticationFilter userJwtAuthenticationFilter;

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
                                "/developers/login",
                                "/auth/**",
                                "/oauth/**"
                        ).permitAll()

                        // Only DEVELOPERS can create apps
                        .requestMatchers(HttpMethod.POST, "/applications/**").hasRole("DEVELOPER")
                        .requestMatchers(HttpMethod.GET, "/applications/**").hasRole("DEVELOPER")
                        .requestMatchers(HttpMethod.PUT, "/applications/**").hasRole("DEVELOPER")
                        .requestMatchers(HttpMethod.DELETE, "/applications/**").hasRole("DEVELOPER")

                        // Only USERS
                        .requestMatchers(HttpMethod.POST, "/user/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/user/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/user/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/user/**").hasRole("USER")


                        // Admin-only endpoints (future)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        //Everything else requires authentication
                        .anyRequest().authenticated()
                )

                .httpBasic(httpBasic -> httpBasic.disable()) // disabling basic auth
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(userJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
