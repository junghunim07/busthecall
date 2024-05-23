package capston.busthecall.controller;

import capston.busthecall.domain.dto.request.RouteBusStopListRequest;
import capston.busthecall.domain.dto.response.RouteBusStopListResponse;
import capston.busthecall.exception.AppException;
import capston.busthecall.route.RouteApiManager;
import capston.busthecall.route.dto.RouteInfoDTO;
import capston.busthecall.service.RouteService;
import capston.busthecall.support.ApiResponse;
import capston.busthecall.support.ApiResponseGenerator;
import capston.busthecall.support.MessageCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final RouteApiManager routeApiManager;
    private final RouteService routeService;

    @GetMapping
    public ApiResponse<ApiResponse.SuccessBody<List<RouteInfoDTO>>> routeInfo() {
        try {
            List<RouteInfoDTO> routeList = routeApiManager.fetch();
            routeService.save(routeList);
            return ApiResponseGenerator.success(routeList, HttpStatus.OK);
        } catch (AppException e) {
            log.info("RouteController : route info error", e);
            return null;
        }
    }

    @PostMapping("/list")
    public ApiResponse<ApiResponse.SuccessBody<List<RouteBusStopListResponse>>> routeListInfo(@RequestBody RouteBusStopListRequest dto) {
        try {
            return ApiResponseGenerator.success(routeApiManager.busStopList(routeService.findOne(dto.getName()))
                    , HttpStatus.OK);
        } catch (AppException e) {
            log.info("RouteController : bus stop list error", e);
            return ApiResponseGenerator.success(null, HttpStatus.NOT_FOUND, MessageCode.NOT_FOUND_ROUTE_NAME);
        }
    }
}
