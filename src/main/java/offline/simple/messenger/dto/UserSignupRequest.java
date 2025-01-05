package offline.simple.messenger.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignupRequest {
    private String username;
    private String password;
    private String name;
}
