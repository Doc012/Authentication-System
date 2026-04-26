package backend.auth.oauth.cotroller;

import backend.auth.oauth.service.GoogleOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final GoogleOAuthService googleOAuthService;

    // 1. Redirecting to Google
    @GetMapping("/google")
    public void redirectToGoogle(
            @RequestParam String apiKey,
            HttpServletResponse response
    ) throws IOException {
        String redirectUrl = googleOAuthService.buildGoogleRedirectUrl(apiKey);
        response.sendRedirect(redirectUrl);
    }

    // 2. Callback from Google
    @GetMapping("/google/callback")
    public ResponseEntity<?> handleGoogleCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state
    ){
        String jwt = googleOAuthService.handleGoogleCallback(code, state);

        return ResponseEntity.ok(Map.of(
                "token", jwt
        ));
    }
}
