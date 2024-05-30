package capston.busthecall.service;

import capston.busthecall.domain.Bus;
import capston.busthecall.domain.Driver;
import capston.busthecall.domain.dto.response.BusArrivalInfo;
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
    public void save(List<BusArrivalInfo> busList) {

        for (BusArrivalInfo bus : busList) {

            if (checkValidation(bus)) {
                busRepository.save(create(bus.getBusId(), bus.getLineId(), 0, 0));
            }
        }
    }

    private boolean checkValidation(BusArrivalInfo bus) {

        if (busRepository.findById(bus.getBusId()).isEmpty()) {
            return true;
        }
        return false;
    }

    @Transactional
    public void update(Long busId, int updateOn, int updateOff) {
        Optional<Bus> bus = busRepository.findById(busId);

        if (bus.isPresent()) {
            Bus updateBus = create(bus.get().getId(), bus.get().getRoute().getId(),
                    bus.get().getRideOn() - updateOn, bus.get().getRideOff() - updateOff);

            busRepository.save(updateBus);
        }
    }

    @Transactional
    public Bus matchBusAndDriver(List<BusInfoDto> list, Long driverId) {

        Bus bus = findOne(list);

        if (bus.getDriver() == null) {
            Driver driver = driverService.findOne(driverId);

            if (driver == null) {
                throw  new IllegalStateException("Invalid driverId: " + driverId);
            }

            Bus updateBus = matchUpdateBus(bus, driver);
            busRepository.save(updateBus);

            return updateBus;
        } else {
            throw new IllegalStateException("The bus already has a driver.");
        }
    }

    public Bus findOne(Long busId) {
        return busRepository.findById(busId).orElse(null);
    }

    private Bus findOne(List<BusInfoDto> list) {

        Optional<Bus> bus = Optional.empty();

        for (BusInfoDto dto : list) {
            bus = busRepository.findById(dto.getBusId());
        }

        return bus.orElse(null);
    }

    private Bus create(Long busId, Long routeId, int on, int off) {

        return Bus.builder()
                .id(busId)
                .route(routeRepository.findById(routeId).orElse(null))
                .rideOn(on)
                .rideOff(off)
                .build();
    }

    private Bus matchUpdateBus(Bus bus, Driver driver) {
        return Bus.builder()
                .id(bus.getId())
                .route(bus.getRoute())
                .driver(driver)
                .busNumber(bus.getBusNumber())
                .rideOn(bus.getRideOn())
                .rideOff(bus.getRideOff())
                .build();
    }
}
