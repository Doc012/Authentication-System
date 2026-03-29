package backend.application.controller;


import backend.application.dto.ApplicationResponse;
import backend.application.dto.CreateApplicationRequest;
import backend.application.entity.Application;
import backend.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping("/applications")
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
}
