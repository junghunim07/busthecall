package capston.busthecall.schedule;

import capston.busthecall.domain.Beacon;
import capston.busthecall.domain.Bus;
import capston.busthecall.domain.dto.response.BusArrivalInfo;
import capston.busthecall.fcm.FirebaseCloudMessageService;
import capston.busthecall.manager.OpenApiManager;
import capston.busthecall.service.BeaconService;
import capston.busthecall.service.BusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final OpenApiManager openApiManager;
    private final BeaconService beaconService;
    private final BusService busService;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final HashMap<Long, List<Long>> check = new HashMap<>();

    @Scheduled(fixedRate = 10000)
    public void fetchBusArrivalInfo() throws IOException {
        List<Beacon> beacons = beaconService.findAll();

        for (Beacon beacon : beacons) {
            List<BusArrivalInfo> infos = openApiManager.fetch(beacon.getStationId());

            for (BusArrivalInfo info : infos) {
                init(info);
                List<Long> stations = check.get(info.getBusId());

                checkExistBeforePushAlarm(beacon, info, stations);
            }
        }
    }

    private void checkExistBeforePushAlarm(Beacon beacon, BusArrivalInfo info, List<Long> stations) throws IOException {
        if (!stations.contains(beacon.getStationId())) {
            callPushLogic(info);
            stations.add(beacon.getStationId());
            check.put(info.getBusId(), stations);

            for (Long station : stations) {
                log.info("busId = {}, stationId = {}", info.getBusId(), station);
            }
        }
    }

    private void init(BusArrivalInfo info) {
        if (!check.containsKey(info.getBusId())) {
            check.put(info.getBusId(), new ArrayList<>());
        }
    }

    private void callPushLogic(BusArrivalInfo info) throws IOException {
        if (info.getRemainMin() < 3) {
            log.info("Bus is RemainMinStop is in 3 = {}", info.getShortLineName());
            //bus 승하차 인원수 알림 보내는 로직.
            Bus bus = busService.findOne(info.getBusId());
            firebaseCloudMessageService.sendMessageTo(bus.getDriver().getFirebase(),
                    info.getBusstopName() + "예약", info.getBusstopName()
                            + ", 승차" + bus.getRideOn() + ", 하차" + bus.getRideOff());
        }
    }
}
