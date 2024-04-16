package capston.busthecall.security.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TokenName {

    ACCESS("access"), REFRESH("refresh");
    private final String name;
}
