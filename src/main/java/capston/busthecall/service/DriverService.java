package capston.busthecall.service;

import capston.busthecall.domain.Driver;
import capston.busthecall.domain.dto.response.SavedInfo;
import capston.busthecall.repository.DriverRepository;
import capston.busthecall.security.authentication.authority.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public SavedInfo join(String name, String email, String password, String firebase) {

        Optional<Driver> existedDriver = driverRepository.findByEmail(email);
        
        if (existedDriver.isPresent()) {
            Driver driver = existedDriver.get();
            return createResponse(driver, false);
        }

        //저장
        Driver driver = save(name, email, password, firebase);

        return createResponse(driver, true);
    }

    public Driver findOne(Long memberId) {

        return driverRepository.findById(memberId).orElse(null);
    }

    public List<Driver> findAll() {

        return driverRepository.findAll();
    }

    private Driver save(String name, String email, String password, String firebase) {
        return driverRepository.save(createDriver(name, email, password, firebase));
    }

    private static SavedInfo createResponse(Driver driver, Boolean registered) {
        return SavedInfo.builder()
                .id(driver.getId())
                .roles(Roles.DRIVER)
                .isRegistered(registered)
                .build();
    }

    private Driver createDriver(String name, String email, String password, String firebase) {

        return Driver.builder()
                .name(name)
                .email(email)
                .password(encoder.encode(password))
                .firebase(firebase)
                .build();
    }
}
