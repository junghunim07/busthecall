package capston.busthecall.service;

import capston.busthecall.domain.Bus;
import capston.busthecall.domain.dto.response.BusArrivalInfo;
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

        if (bus.getShortLineName().equals("진월07")) {
            if (busRepository.findById(bus.getBusId()).isEmpty()) {
                return true;
            }
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

    public Bus findOne(Long busId) {
        return busRepository.findById(busId).orElse(null);
    }

    // 드라이버 매칭에 대해서 고민해 봐야함.
    private Bus create(Long busId, Long routeId, int on, int off) {

        return Bus.builder()
                .id(busId)
                .driver(driverService.findOne(1L))
                .route(routeRepository.findById(routeId).orElse(null))
                .rideOn(on)
                .rideOff(off)
                .build();
    }
}
