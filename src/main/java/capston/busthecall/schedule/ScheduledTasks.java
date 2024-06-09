package capston.busthecall.schedule;

import capston.busthecall.domain.Beacon;
import capston.busthecall.domain.Bus;
import capston.busthecall.domain.dto.response.BusArrivalInfo;
import capston.busthecall.exception.AppException;
import capston.busthecall.exception.ErrorCode;
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
    public void fetchBusArrivalInfo() {
        List<Beacon> beacons = beaconService.findAll();

        for (Beacon beacon : beacons) {
            extractStationInfoIn(beacon);
        }
    }

    private void extractStationInfoIn(Beacon beacon) {
        List<BusArrivalInfo> infos = openApiManager.fetch(beacon.getStationId());

        for (BusArrivalInfo info : infos) {
            processEach(beacon, info);
        }
    }

    private void processEach(Beacon beacon, BusArrivalInfo info) {
        init(info);
        List<Long> stations = check.get(info.getBusId());

        log.info("process Each : Scheduled Tasks bus ID = {}", info.getBusId());
        checkExistBeforePushAlarm(beacon, info, stations, beacon.getStationName());
    }

    private void init(BusArrivalInfo info) {
        if (!check.containsKey(info.getBusId())) {
            check.put(info.getBusId(), new ArrayList<>());
        }
    }

    private void checkExistBeforePushAlarm(Beacon beacon, BusArrivalInfo info, List<Long> stations, String nowStationName) {
        log.info("check Exist Before Push Alarm");
        if (!stations.contains(beacon.getStationId())) {
            try {
                sendMessageOrNot(beacon, info, stations, nowStationName);
            } catch (AppException e) {
                log.info("push alarm error", e);
            } catch (IOException e) {
                log.info("generate IOException", e);
            }
        }
    }

    private void sendMessageOrNot(Beacon beacon, BusArrivalInfo info, List<Long> stations, String nowStationName) throws IOException {
        if (callPushLogic(info, nowStationName)) {
            stations.add(beacon.getStationId());
            check.put(info.getBusId(), stations);

            for (Long station : stations) {
                log.info("busId = {}, stationId = {}", info.getBusId(), station);
            }
        }
    }

    private boolean callPushLogic(BusArrivalInfo busInfo, String stationName) throws IOException {
        log.info("Bus Call Push Logic : bus remain stop : {}", busInfo.getRemainStop());
        if (busInfo.getRemainStop() <= 6) {
            log.info("Bus is Remain Stop is in next stop bus = {}", busInfo.getShortLineName());
            //bus 승하차 인원수 알림 보내는 로직.
            Bus bus = busService.findOne(busInfo.getBusId());

            if (bus != null) {
                firebaseCloudMessageService.sendMessageTo(bus.getDriver().getFirebase(),
                        stationName + " 예약 정보",
                        "승차 " + bus.getRideOn() + "명, 하차 " + bus.getRideOff() + "명");
                return true;
            } else {
                throw new AppException(ErrorCode.NOT_FOUND_OPERATING_BUS, "call push error");
            }
        }
        log.info("Call Push Logic finish");
        return false;
    }
}
