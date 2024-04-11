package capston.busthecall.domain.dto.response;

import capston.busthecall.support.token.AuthToken;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SavedUserInfo {

    private Long id;
    private AuthToken token;
    private Boolean isRegistered;
}