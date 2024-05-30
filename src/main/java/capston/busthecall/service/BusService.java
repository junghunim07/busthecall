package capston.busthecall.service;

import capston.busthecall.domain.Bus;
import capston.busthecall.domain.Driver;
import capston.busthecall.domain.dto.response.BusArrivalInfo;
import capston.busthecall.exception.AppException;
import capston.busthecall.exception.ErrorCode;
import capston.busthecall.manager.dto.BusInfoDto;
import capston.busthecall.repository.BusRepository;
import capston.busthecall.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;
    private final DriverService driverService;
    private final RouteRepository routeRepository;

    @Transactional
    public Bus save(Long driverId, List<BusArrivalInfo> arrivalInfos, List<BusInfoDto> busInfos) {

        BusInfoDto busInfo = getBusInfo(arrivalInfos, busInfos);
        Bus bus = create(driverService.findOne(driverId), busInfo.getBusId()
                , busInfo.getRouteId(), busInfo.getBusNumber(), 0, 0);
        busRepository.save(bus);

        return bus;
    }

    @Transactional
    public void update(Long busId, int updateOn, int updateOff) {
        Optional<Bus> bus = busRepository.findById(busId);

        if (bus.isPresent()) {
            Bus updateBus = create(bus.get().getDriver(), bus.get().getId(), bus.get().getRoute().getId(),
                    bus.get().getBusNumber(),bus.get().getRideOn() - updateOn
                    , bus.get().getRideOff() - updateOff);

            busRepository.save(updateBus);
        }
    }

    @Transactional
    public void finish(Long busId) {
        Optional<Bus> bus = busRepository.findById(busId);

        if (bus.isEmpty()) {
            throw new AppException(ErrorCode.NOT_FOUND_OPERATING_BUS, "not operating bus");
        } else {
            busRepository.delete(bus.get());
        }
    }

    public Bus findOne(Long busId) {
        return busRepository.findById(busId).orElse(null);
    }

    private Bus create(Driver driver, Long busId, Long routeId, String busNumber, int on, int off) {

        return Bus.builder()
                .id(busId)
                .driver(driver)
                .route(routeRepository.findById(routeId).orElse(null))
                .busNumber(busNumber)
                .rideOn(on)
                .rideOff(off)
                .build();
    }

    private BusInfoDto getBusInfo(List<BusArrivalInfo> arrivalInfos, List<BusInfoDto> busInfos) {

        BusInfoDto dto = new BusInfoDto();
        for (BusArrivalInfo arrivalInfo : arrivalInfos) {
            for (BusInfoDto busInfo : busInfos) {
                if (arrivalInfo.getBusId().equals(busInfo.getBusId())) {
                    dto = busInfo;
                }
            }
        }
        return dto;
    }
}
