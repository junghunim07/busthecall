package capston.busthecall.controller;

import capston.busthecall.domain.dto.response.BusArrivalInfo;
import capston.busthecall.manager.OpenApiManager;
import capston.busthecall.repository.BeaconRepository;
import capston.busthecall.security.token.TokenResolver;
import capston.busthecall.service.BeaconService;
import capston.busthecall.service.BusService;
import capston.busthecall.support.ApiResponse;
import capston.busthecall.support.ApiResponseGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/list")
@Slf4j
public class BusArrivalInfoController {

    private final OpenApiManager openApiManager;
    private final BeaconService beaconService;
    private final BusService busService;

    @GetMapping("/{uuId}")
    public ApiResponse<ApiResponse.SuccessBody<List<BusArrivalInfo>>> getBusArrivalInfo(@PathVariable("uuId") String uuId) {
        try {
            Long stationId = beaconService.excute(uuId);
            List<BusArrivalInfo> busArrivalInfos = openApiManager.fetch(stationId);
            return ApiResponseGenerator.success(busArrivalInfos, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching bus arrival information", e);
            return null;
        }
    }
}