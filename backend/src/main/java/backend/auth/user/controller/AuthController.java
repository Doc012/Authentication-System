package backend.auth.user.controller;

import backend.auth.user.dto.UserLoginRequest;
import backend.auth.user.dto.UserSignupRequest;
import backend.auth.user.entity.User;
import backend.auth.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    public String signup(
            @RequestHeader("x-api-key") String apiKey,
            @RequestBody UserSignupRequest request
    ){
        User user = userService.signup(
                request.getEmail(),
                request.getPassword(),
                apiKey
        );

        return "User created with ID: " + user.getId();
    }

    @PostMapping("/login")
    public String login(
            @RequestHeader("x-api-key") String apiKey,
            @RequestBody UserLoginRequest request
    ){
        return userService.login(
                request.getEmail(),
                request.getPassword(),
                apiKey
        );
    }
}
