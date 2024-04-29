package capston.busthecall.controller;

import capston.busthecall.domain.dto.request.JoinRequest;
import capston.busthecall.domain.dto.response.SavedInfo;
import capston.busthecall.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/join")
    public ResponseEntity<SavedInfo> join(@RequestBody JoinRequest dto) {
        return ResponseEntity.ok().body(driverService.join(dto.getName()
                , dto.getEmail(), dto.getPassword()));
    }
}
