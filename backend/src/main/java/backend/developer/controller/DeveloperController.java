package backend.developer.controller;

import backend.developer.dto.AuthResponse;
import backend.developer.dto.DeveloperLoginRequest;
import backend.developer.dto.DeveloperResponse;
import backend.developer.dto.DeveloperSignupRequest;
import backend.developer.entity.Developer;
import backend.developer.service.DeveloperService;
import backend.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/developers")
@RequiredArgsConstructor
public class DeveloperController {
    private final DeveloperService developerService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public DeveloperResponse signup(@RequestBody DeveloperSignupRequest request){
        Developer developer = developerService.register(
                request.getEmail(),
                request.getPassword()
        );

        return DeveloperResponse.builder()
                .id(developer.getId())
                .email(developer.getEmail())
                .build();
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody DeveloperLoginRequest request){
        Developer developer = developerService.login(
                request.getEmail(),
                request.getPassword()
        );

        String token = jwtService.generateToken(
                developer.getId(),
                developer.getEmail(),
                developer.getRole().name()
        );

        return AuthResponse.builder()
                .accessToken(token)
                .build();
    }
}
