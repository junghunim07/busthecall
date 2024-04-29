package capston.busthecall.service;

import capston.busthecall.domain.Driver;
import capston.busthecall.domain.dto.response.SavedInfo;
import capston.busthecall.repository.DriverRepository;
import capston.busthecall.security.authentication.authority.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public SavedInfo join(String name, String email, String password) {

        Optional<Driver> existedDriver = driverRepository.findByEmail(email);
        
        if (existedDriver.isPresent()) {
            Driver driver = existedDriver.get();
            return createResponse(driver, false);
        }

        //저장
        Driver driver = createDriver(name, email, password);
        driverRepository.save(driver);

        return createResponse(driver, true);
    }

    private static SavedInfo createResponse(Driver driver, Boolean registered) {
        return SavedInfo.builder()
                .id(driver.getId())
                .roles(Roles.DRIVER)
                .isRegistered(registered)
                .build();
    }

    private Driver createDriver(String name, String email, String password) {

        return Driver.builder()
                .name(name)
                .email(email)
                .password(encoder.encode(password))
                .build();
    }
}
