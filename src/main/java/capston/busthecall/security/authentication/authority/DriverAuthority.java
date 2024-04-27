package capston.busthecall.security.authentication.authority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@Getter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
public class DriverAuthority implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return Roles.DRIVER.getRole();
    }
}
