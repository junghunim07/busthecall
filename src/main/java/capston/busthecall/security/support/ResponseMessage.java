package capston.busthecall.security.support;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseMessage {

    LOGIN_SUCCESS("LogIn Success"), LOGIN_FAIL("LogIn Fail"), REISSUE_REFRESH_TOKEN("JWT Reissue");
    private final String message;
}
