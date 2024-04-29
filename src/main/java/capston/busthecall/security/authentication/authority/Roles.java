package capston.busthecall.security.authentication.authority;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Getter
@ToString
public enum Roles {
    MEMBER("ROLE_MEMBER") {
        @Override
        public GrantedAuthority getAuthority() {
            return MemberAuthority.builder().build();
        }
    }, DRIVER("ROLE_DRIVER") {
        @Override
        public GrantedAuthority getAuthority() {
            return DriverAuthority.builder().build();
        }
    };

    Roles(String role) {
        this.role = role;
    }

    private final String role;

    public abstract GrantedAuthority getAuthority();

    public static List<Roles> roleOf(String role) {
        for (Roles roles : Roles.values()) {
            if (roles.getRole().equals(role)) {
                return List.of(roles);
            }
        }
        throw new IllegalArgumentException("Invalid role. role: " + role);
    }
}