package capston.busthecall.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, ""),
    EMAIL_NOTFOUND(HttpStatus.NOT_FOUND, ""),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, ""),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "not refresh token in DB"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "refresh token expired"),
    NOT_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "not refresh token"),
    NULL_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "refresh token is null");

    private HttpStatus status;
    private String message;
}
