package capston.busthecall.controller;

import capston.busthecall.domain.dto.request.JoinRequest;
import capston.busthecall.domain.dto.response.SavedInfo;
import capston.busthecall.service.DriverService;
import capston.busthecall.support.ApiResponse;
import capston.busthecall.support.ApiResponseGenerator;
import capston.busthecall.support.MessageCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/join")
    public ApiResponse<ApiResponse.SuccessBody<SavedInfo>> join(@RequestBody JoinRequest dto) {
        SavedInfo driver = driverService.join(dto.getName(), dto.getEmail(), dto.getPassword(), dto.getFirebase());

        if (!driver.getIsRegistered()) {
            log.info("Driver is Already Existed");
            return ApiResponseGenerator.success(driver, HttpStatus.UNAUTHORIZED, MessageCode.ALREADY_EXIST);
        }

        return ApiResponseGenerator.success(driver, HttpStatus.CREATED);
    }
}
