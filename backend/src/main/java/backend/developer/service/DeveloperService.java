package backend.developer.service;

import backend.application.repository.ApplicationRepository;
import backend.developer.entity.Developer;
import backend.developer.enums.Role;
import backend.developer.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeveloperService {

    private final DeveloperRepository developerRepository;
    private final PasswordEncoder passwordEncoder;

    public Developer register(String email, String password) {
        // 1. Checking is email already exists
        if (developerRepository.existsByEmail(email)) {
            throw new RuntimeException(("Email already in use"));
        }

        // 2. Hashing the password
        String hashedPassword = passwordEncoder.encode(password);

        // 3. Create developer (object)
        Developer developer =  Developer.builder()
                .email(email)
                .passwordHash(hashedPassword)
                .plan("FREE")
                .role(Role.ROLE_DEVELOPER)
                .build();

        // 4. Save
        return developerRepository.save(developer);

    }

    public Developer login(String email, String password){
        Developer developer = developerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, developer.getPasswordHash())) {
                throw new RuntimeException("Invalid credentials");
        }

        return developer;
    }
}
