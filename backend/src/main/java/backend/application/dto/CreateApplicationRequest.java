package backend.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateApplicationRequest {
    @NotBlank
    private String name;

    private String description;
}
