package backend.auth.oauth.dto;

import lombok.Data;

@Data
public class GoogleUserInfo {
    private String sub;
    private String email;
    private String name;

}
