package capston.busthecall.security.dto.custom;

import capston.busthecall.domain.Member;
import capston.busthecall.security.authentication.authority.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomMemberDetails implements CustomDetails {

    private final Member member;

    @Override
    public Long getId() {
        return member.getId();
    }

    @Override
    public String getEmail() {
        return member.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(Roles.MEMBER.getAuthority());

        return collection;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getName();
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
