package backend.developer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeveloperLoginRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
