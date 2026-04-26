package backend.application.service;

import backend.application.entity.Application;
import backend.application.repository.ApplicationRepository;
import backend.developer.entity.Developer;
import backend.developer.repository.DeveloperRepository;
import backend.security.util.KeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final DeveloperRepository developerRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. Creating Application
    public Application createApplication(String name, String description) {
        //1. Extracting developerId from JWT
        UUID developerId = getCurrentDeveloperId();

        // 2. Finding the developer
        Developer developer = developerRepository.findById(developerId)
                .orElseThrow(() -> new RuntimeException("Developer not found"));

        // 3. (default) Generate keys
        String apiKey = KeyGenerator.generateKey("ak");
        String clientId = KeyGenerator.generateKey("cid");
        String rawClientSecret = KeyGenerator.generateKey("cs");

        // 4. (default) Hash client secret
        String clientSecretHash = passwordEncoder.encode(rawClientSecret);

        // 5. Create Application
        Application app = Application.builder()
                .name(name)
                .description(description)
                .developer(developer)
                .apiKey(apiKey)
                .clientId(clientId)
                .clientSecretHash(clientSecretHash)
                .build();

        Application savedApp = applicationRepository.save(app);

        /**
            VERY IMPORTANT (Real-World Behavior)
            IMPORTANT: return raw secret (only once)
            Client Secret is ONLY shown ONCE during creation
        */
        savedApp.setClientSecretHash(rawClientSecret);

        // 4. save
        return savedApp;
    }

    // 2. Helper Method (Reusable Everywhere)
    private UUID getCurrentDeveloperId(){
        return (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // 3. Get All Applications
    public List<Application> getAllApplications(){
        UUID developerId = getCurrentDeveloperId();
        return applicationRepository.findByDeveloperId(developerId);
    }

    // 4. Get Application by ID
    public Application getApplicationById(UUID appId) {
        UUID developerId = getCurrentDeveloperId();
        return applicationRepository.findByIdAndDeveloperId(appId, developerId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    // 5. Updating Application
    public Application updateApplication(UUID appId, String name, String description){
        UUID developerId = getCurrentDeveloperId();

        Application app = applicationRepository
                .findByIdAndDeveloperId(appId, developerId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        app.setName(name);
        app.setDescription(description);

        return applicationRepository.save(app);
    }

    // 6. Deleting Application
    public void deleteApplication(UUID appId){
        UUID developerId = getCurrentDeveloperId();

        Application app = applicationRepository
                .findByIdAndDeveloperId(appId, developerId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        applicationRepository.delete(app);
    }
}
