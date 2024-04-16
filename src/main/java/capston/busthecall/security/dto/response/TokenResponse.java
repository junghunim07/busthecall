package capston.busthecall.security.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
@Data
public class TokenResponse {

    private HttpStatus status;
    private String message;
    private Map<String, String> data;
}
