package backend.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ApplicationResponse {
    private UUID id;
    private String name;
    private String apiKey;
    private String clientId;
    private String clientSecret;
}
