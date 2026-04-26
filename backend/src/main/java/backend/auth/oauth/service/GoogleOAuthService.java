package backend.auth.oauth.service;

import backend.application.entity.Application;
import backend.application.repository.ApplicationRepository;
import backend.auth.oauth.dto.GoogleUserInfo;
import backend.auth.security.jwt.JwtService;
import backend.auth.user.entity.User;
import backend.auth.user.repository.UserRepository;
import backend.developer.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {
    private final JwtService jwtService;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    public String buildGoogleRedirectUrl(String apiKey) {

        String state = jwtService.generateOAuthState(apiKey);

        return "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=http://localhost:8080/oauth/google/callback" +
                "&response_type=code" +
                "&scope=openid%20email%20profile" +
                "&state=" + state;
    }

    public String handleGoogleCallback(String code, String state) {

        String apiKey = jwtService.extractApiKeyFromState(state); // 🔥 NEW

        String accessToken = exchangeCodeForToken(code);
        GoogleUserInfo userInfo = fetchUserInfo(accessToken);
        User user = findOrCreateUser(userInfo, apiKey);

        return jwtService.generateUserToken(
                user.getId(),
                user.getApplication().getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    private String exchangeCodeForToken(String code){

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", "http://localhost:8080/oauth/google/callback");
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                request,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }
    private GoogleUserInfo fetchUserInfo(String accessToken) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                entity,
                GoogleUserInfo.class
        );

        return response.getBody();
    }

    private User findOrCreateUser(GoogleUserInfo info, String apiKey) {

        Application app = applicationRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new RuntimeException("Invalid API Key"));

        // 1. Trying to find existing user by email + app
        Optional<User> existingUser =
                userRepository.findByEmailAndApplicationId(info.getEmail(), app.getId());

        if (existingUser.isPresent()){
            User user = existingUser.get();

            // Linking Google Account with Existing User if Not Linked
            if (user.getProviderId() == null){
                if ("LOCAL".equals(user.getProvider())){
                    user.setProvider("BOTH");
                } else {
                    user.setProvider("GOOGLE");
                }
                user.setProviderId(info.getSub());
                return userRepository.save(user);
            }
            return user;
        }

        // 2. Creating new user
        User newUser = User.builder()
                .email(info.getEmail())
                .provider("GOOGLE")
                .providerId(info.getSub())
                .application(app)
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(newUser);
    }
}
