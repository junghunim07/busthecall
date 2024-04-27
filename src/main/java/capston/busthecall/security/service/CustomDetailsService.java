package capston.busthecall.security.service;

import capston.busthecall.exception.AppException;
import capston.busthecall.exception.ErrorCode;
import capston.busthecall.security.service.custom.CustomDriverDetailsService;
import capston.busthecall.security.service.custom.CustomMemberDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomDetailsService implements UserDetailsService {

    private final CustomMemberDetailsService customMemberDetailsService;
    private final CustomDriverDetailsService customDriverDetailsService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            return customMemberDetailsService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            return customDriverDetailsService.loadUserByUsername(email);
        } catch (AppException e) {
            log.info("error = {}", e.getMessage());
        }

        throw new AppException(ErrorCode.EMAIL_NOTFOUND, "해당 이메일은 존재하지 않습니다.");
    }
}
