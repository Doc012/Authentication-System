package backend.auth.user.service;

import backend.application.entity.Application;
import backend.application.repository.ApplicationRepository;
import backend.auth.user.entity.User;
import backend.auth.user.repository.UserRepository;
import backend.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User signup(String email, String password, String apiKey){
        // 1. Finding an application using API key
        Application app = applicationRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new RuntimeException("Invalid API Key"));

        // 2. Checking if user already exists in this app
        if (userRepository.existsByEmailAndApplicationId(email, app.getId())){
            throw new RuntimeException("User already exists");
        }

        // 3. Hashing the password if user does not exist
        String passwordHash = passwordEncoder.encode(password);

        // 4. Creating the user
        User user = User.builder()
                .email(email)
                .passwordHash(passwordHash)
                .application(app)
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    public String login(String email, String password, String apiKey){
        // 1. Finding the application
        Application app = applicationRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new RuntimeException("Invalid API Key"));

        // 2. Finding User
        User user = userRepository
                .findByEmailAndApplicationId(email, app.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Validating password
        if (!passwordEncoder.matches(password, user.getPasswordHash())){
            throw new RuntimeException("Invalid credentials");
        }

        // 4. Generating JWT
        return jwtService.generateUserToken(
                user.getId(),
                app.getId(),
                user.getEmail()
        );

    }
}
