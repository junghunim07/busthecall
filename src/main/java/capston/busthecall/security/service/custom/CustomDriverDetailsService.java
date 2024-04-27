package capston.busthecall.security.service.custom;

import capston.busthecall.domain.Driver;
import capston.busthecall.security.dto.custom.CustomDriverDetails;
import capston.busthecall.exception.AppException;
import capston.busthecall.exception.ErrorCode;
import capston.busthecall.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomDriverDetailsService implements UserDetailsService {

    private final DriverRepository driverRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Driver> driverOptional = driverRepository.findByEmail(email);

        if (driverOptional.isPresent()) {
            log.info("CustomDriverDetailsService : driverName = {}", driverOptional.get().getName());
            return new CustomDriverDetails(driverOptional.get());
        }

        throw new AppException(ErrorCode.EMAIL_NOTFOUND, "해당 이메일은 Driver 에 존재하지 않습니다.");
    }
}
