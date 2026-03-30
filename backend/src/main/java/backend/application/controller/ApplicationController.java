package backend.application.controller;


import backend.application.dto.ApplicationResponse;
import backend.application.dto.CreateApplicationRequest;
import backend.application.entity.Application;
import backend.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    // 1. Creating Applications
    @PostMapping()
    public ApplicationResponse createApplication(
            @RequestBody CreateApplicationRequest request
    ){
        Application app = applicationService.createApplication(
                request.getName(),
                request.getDescription()
        );

        return ApplicationResponse.builder()
                .id(app.getId())
                .name(app.getName())
                .build();
    }

    // 2. Getting all Application
    @GetMapping
    public List<ApplicationResponse> getAllApplications(){
        return applicationService.getAllApplications()
                .stream()
                .map(app -> ApplicationResponse.builder()
                        .id(app.getId())
                        .name(app.getName())
                        .build())
                .toList();
    }

    // 3. Getting Application by ID
    @GetMapping("/{id}")
    public ApplicationResponse getApplication(@PathVariable UUID id){
        Application app = applicationService.getApplicationById(id);

        return ApplicationResponse.builder()
                .id(app.getId())
                .name(app.getName())
                .build();
    }



    // 4. Updating Application
    public ApplicationResponse updateApplication(
            @PathVariable UUID id, @RequestBody CreateApplicationRequest request
    ){
        Application app = applicationService.updateApplication(
                id,
                request.getName(),
                request.getDescription()
        );

        return ApplicationResponse.builder()
                .id(app.getId())
                .name(app.getName())
                .build();
    }

    // 5. Deleting Application
    @DeleteMapping("/{id}")
    public void deleteApplication(@PathVariable UUID id){
        applicationService.deleteApplication(id);
    }


}
