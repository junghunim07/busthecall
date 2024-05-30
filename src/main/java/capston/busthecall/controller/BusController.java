package capston.busthecall.controller;

import capston.busthecall.domain.Bus;
import capston.busthecall.domain.dto.request.OperateInfoRequest;
import capston.busthecall.domain.dto.response.OperateInfoResponse;
import capston.busthecall.manager.BusApiManager;
import capston.busthecall.manager.dto.BusInfoDto;
import capston.busthecall.security.token.TokenResolver;
import capston.busthecall.service.BusService;
import capston.busthecall.service.RouteService;
import capston.busthecall.support.ApiResponse;
import capston.busthecall.support.ApiResponseGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/bus")
public class BusController {

    private final TokenResolver tokenResolver;
    private final RouteService routeService;
    private final BusService busService;
    private final BusApiManager busApiManager;

    /**
     * 버스 객체 생성은 고민해봐야 할듯.
     * 버스 객체는 버스 기사가 운행 시 -> 객체 생성.
     * 생성된 버스 객체는 예약을 할 수 있도록 DB에 데이터 저장.
      */
    /*@GetMapping
    public ApiResponse<ApiResponse.SuccessBody<>> busInformation() {

    }*/

    @PostMapping("/operate")
    public ApiResponse<ApiResponse.SuccessBody<OperateInfoResponse>> operate(
            @RequestBody OperateInfoRequest dto, HttpServletRequest request) {

        Long driverId = findDriverByToken(request);
        Long routeId = routeService.findOne(dto.getRouteName());
        List<BusInfoDto> busInfo = busApiManager.getBusNumber(routeId);

        try {
            Bus bus = busService.matchBusAndDriver(busInfo, driverId);
            return ApiResponseGenerator.success(createResponse(bus), HttpStatus.OK);
        } catch (Exception e) {
            log.info("bus operate Controller Error");
            return null;
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
