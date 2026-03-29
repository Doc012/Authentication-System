package backend.application.service;

import backend.application.entity.Application;
import backend.application.repository.ApplicationRepository;
import backend.developer.entity.Developer;
import backend.developer.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final DeveloperRepository developerRepository;

    public Application createApplication(String name, String description) {
        //1. Extracting developerId from JWT
        UUID developerId = (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();


        // 2. Finding the developer
        Developer developer = developerRepository.findById(developerId)
                .orElseThrow(() -> new RuntimeException("Developer not found"));

        // 3. Creating application
        Application app = Application.builder()
                .name(name)
                .description(description)
                .developer(developer)
                .build();

        // 4. save
        return applicationRepository.save(app);
    }
}
