package capston.busthecall.controller;

import capston.busthecall.domain.Beacon;
import capston.busthecall.domain.Bus;
import capston.busthecall.domain.dto.request.OperateInfoRequest;
import capston.busthecall.domain.dto.response.BusArrivalInfo;
import capston.busthecall.domain.dto.response.OperateInfoResponse;
import capston.busthecall.exception.AppException;
import capston.busthecall.manager.BusApiManager;
import capston.busthecall.manager.OpenApiManager;
import capston.busthecall.manager.dto.BusInfoDto;
import capston.busthecall.security.token.TokenResolver;
import capston.busthecall.service.BeaconService;
import capston.busthecall.service.BusService;
import capston.busthecall.service.RouteService;
import capston.busthecall.support.ApiResponse;
import capston.busthecall.support.ApiResponseGenerator;
import capston.busthecall.support.MessageCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/bus")
public class BusController {

    private final TokenResolver tokenResolver;
    private final RouteService routeService;
    private final BusService busService;
    private final BeaconService beaconService;
    private final BusApiManager busApiManager;
    private final OpenApiManager openApiManager;

    /**
     * 버스 객체 생성은 고민해봐야 할듯.
     * 버스 객체는 버스 기사가 운행 시 -> 객체 생성.
     * 생성된 버스 객체는 예약을 할 수 있도록 DB에 데이터 저장.
     */
    @PostMapping("/operate")
    public ApiResponse<ApiResponse.SuccessBody<OperateInfoResponse>> operate(
            @RequestBody OperateInfoRequest dto, HttpServletRequest request) {

        Long driverId = findDriverByToken(request);

        if (driverId == null) {
            log.info("driver error");
            return ApiResponseGenerator.success(null, HttpStatus.BAD_REQUEST, MessageCode.NOT_FOUND_DRIVER);
        }

        List<BusInfoDto> busInfos = busApiManager.getBusNumber(routeService.findOne(dto.getRouteName()));
        List<Beacon> beacons = beaconService.findAll();
        List<BusArrivalInfo> busArrivalInfos = new ArrayList<>();

        for (Beacon beacon : beacons) {
            busArrivalInfos = openApiManager.fetch(beacon.getStationId());
        }

        try {
            return ApiResponseGenerator.success(createResponse(busService
                    .save(driverId, busArrivalInfos, busInfos)), HttpStatus.OK);
        } catch (Exception e) {
            log.info("bus operate Controller Error");
            return ApiResponseGenerator.success(null, HttpStatus.OK, MessageCode.NOT_FOUND_BUS);
        }
    }

    @GetMapping("/finish/{busId}")
    public ApiResponse<ApiResponse.SuccessBody<Void>> finish(@PathVariable("busId") Long busId) {
        try {
            busService.finish(busId);
            return ApiResponseGenerator.success(HttpStatus.OK, MessageCode.FINISH_OPERATE_BUS);
        } catch (AppException e) {
            log.info("delete bus error in finish method", e);
            return ApiResponseGenerator.success(HttpStatus.BAD_REQUEST, MessageCode.NOT_FOUND_BUS);
        }
    }

    private static OperateInfoResponse createResponse(Bus bus) {
        return OperateInfoResponse.builder()
                .busId(bus.getId())
                .routeId(bus.getRoute().getId())
                .routeName(bus.getRoute().getName())
                .busNumber(bus.getBusNumber())
                .build();
    }

    private Long findDriverByToken(HttpServletRequest request) {
        return tokenResolver.getId(request.getHeader("access"));
    }
}
