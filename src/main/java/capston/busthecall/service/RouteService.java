package capston.busthecall.service;

import capston.busthecall.domain.Route;
import capston.busthecall.exception.AppException;
import capston.busthecall.exception.ErrorCode;
import capston.busthecall.repository.RouteRepository;
import capston.busthecall.route.dto.RouteInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouteService {

    private final RouteRepository routeRepository;

    @Transactional
    public void save(List<RouteInfoDTO> routeInfoDTOList) {
        if (routeInfoDTOList.isEmpty()) {
            throw new AppException(ErrorCode.ROUTE_ENCODING_ERROR, "route information list is empty");
        }

        for (RouteInfoDTO routeInfoDTO : routeInfoDTOList) {
            if (routeRepository.findById(routeInfoDTO.getRouteId()).isEmpty()) {
                Route route = Route.builder()
                        .id(routeInfoDTO.getRouteId())
                        .name(routeInfoDTO.getRouteName())
                        .kind(routeInfoDTO.getRouteKind())
                        .build();

                routeRepository.save(route);
            }

            if (routeRepository.findById(routeInfoDTO.getRouteId()).isPresent()) {
                Route route = routeRepository.findById(routeInfoDTO.getRouteId()).get();
                log.info("duplicate Route Id = {}, Route Name = {}", routeInfoDTO.getRouteId(), routeInfoDTO.getRouteName());
                log.info("In DB : Route Id = {}, Route Name = {}", route.getId(), route.getName());
            }
        }
    }

    public Long findOne(String routeName) {
        Optional<Route> route = routeRepository.findByName(routeName);
        if (route.isPresent()) {
            return route.get().getId();
        }
        throw new AppException(ErrorCode.ROUTE_NOT_EXIST, "Not Exist In DB");
    }
}
