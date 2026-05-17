package backend.developer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeveloperSignupRequest {
    @Email
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Password can not be empty")
    private String password;
}
