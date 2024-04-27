package capston.busthecall.security.dto.custom;

import capston.busthecall.domain.Driver;
import capston.busthecall.security.authentication.authority.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomDriverDetails implements CustomDetails {

    private final Driver driver;

    //role 값 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(Roles.DRIVER.getAuthority());

        return collection;
    }

    public Long getId() {
        return driver.getId();
    }

    @Override
    public String getPassword() {
        return driver.getPassword();
    }

    @Override
    public String getUsername() {
        return driver.getName();
    }

    public String getEmail() {
        return driver.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
