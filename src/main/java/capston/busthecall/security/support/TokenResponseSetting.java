package capston.busthecall.security.support;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenResponseSetting {

    CONTENT_TYPE_JSON("application/json"), CHARACTER_ENCODING_UTF8("UTF-8");

    private final String setting;
}
