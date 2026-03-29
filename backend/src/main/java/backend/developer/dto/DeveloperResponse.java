package backend.developer.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DeveloperResponse {
    private UUID id;
    private String email;
}
