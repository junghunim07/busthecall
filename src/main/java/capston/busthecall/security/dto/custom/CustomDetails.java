package capston.busthecall.security.dto.custom;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomDetails extends UserDetails {

    public Long getId();
    public String getEmail();
}
