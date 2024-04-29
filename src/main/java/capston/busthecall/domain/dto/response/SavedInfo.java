package capston.busthecall.domain.dto.response;

import capston.busthecall.security.authentication.authority.Roles;
import capston.busthecall.support.token.AuthToken;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SavedInfo {

    private Long id;
    private Roles roles;
    private Boolean isRegistered;
}