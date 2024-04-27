package capston.busthecall.security.dto.response;

import capston.busthecall.security.support.ResponseMessage;
import capston.busthecall.security.token.AuthToken;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
@Data
public class TokenResponse {

    private HttpStatus status;
    private ResponseMessage message;
    private AuthToken token;
}
