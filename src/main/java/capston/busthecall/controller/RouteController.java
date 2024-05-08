package capston.busthecall.controller;

import capston.busthecall.domain.dto.request.RouteBusStopListRequest;
import capston.busthecall.exception.AppException;
import capston.busthecall.route.RouteApiManager;
import capston.busthecall.route.dto.RouteInfoDTO;
import capston.busthecall.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/route")
public class RouteController {

    private final RouteApiManager routeApiManager;
    private final RouteService routeService;

    @GetMapping
    public ResponseEntity<List<RouteInfoDTO>> routeInfo() {
        try {
            List<RouteInfoDTO> routeList = routeApiManager.fetch();
            routeService.save(routeList);
            return ResponseEntity.ok().body(routeList);
        } catch (AppException e) {
            log.info("RouteController : error", e);
            return null;
        } catch (Exception e) {
            log.info("Route Api Error", e);
            return null;
        }
    }

    @PostMapping("/list")
    public ResponseEntity<List<String>> routeListInfo(@RequestBody RouteBusStopListRequest dto) {
        try {
            return ResponseEntity.ok()
                    .body(routeApiManager.busStopList(routeService.findOne(dto.getName())));
        } catch (AppException e) {
            log.info("RouteController : error", e);
            return null;
        } catch (Exception e) {
            log.info("Route Api Error", e);
            return null;
        }
    }
}
