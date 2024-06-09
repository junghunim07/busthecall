package capston.busthecall.manager;

import capston.busthecall.domain.dto.request.PredictTimeRequestDto;
import capston.busthecall.domain.dto.response.BusArrivalInfo;
import capston.busthecall.domain.dto.response.PredictTimeResponseDto;
import capston.busthecall.domain.dto.response.RouteBusStopListResponse;
import capston.busthecall.route.RouteApiManager;
import capston.busthecall.service.RouteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AIManager {

    private final String pythonURL;
    private final ObjectMapper objectMapper;
    private final RouteService routeService;
    private final RouteApiManager routeApiManager;

    public AIManager(@Value("${python}") String url, ObjectMapper objectMapper
            , RouteService routeService, RouteApiManager routeApiManager) {
        pythonURL = url + "/predict_time";
        this.objectMapper = objectMapper;
        this.routeService = routeService;
        this.routeApiManager = routeApiManager;
    }

    public List<BusArrivalInfo> predictTime(List<BusArrivalInfo> infos, String arrivalBusStopName) {

        try {
            return createResponse(infos, sendToPythonServer(infos, arrivalBusStopName));
        } catch (Exception e) {
            log.info("predictTime Error", e);
            return infos;
        }
    }

    private List<BusArrivalInfo> createResponse(List<BusArrivalInfo> infos, PredictTimeResponseDto dto) {
        BusArrivalInfo busInfo = getWantBusInfo(infos);
        infos.remove(busInfo);
        log.info("PredictTime : {}", dto.getOperationTime());
        infos.add(updateBusInfo(busInfo, dto));
        return infos;
    }

    private BusArrivalInfo updateBusInfo(BusArrivalInfo info, PredictTimeResponseDto dto) {
        return BusArrivalInfo.builder()
                .busId(info.getBusId())
                .remainStop(info.getRemainStop())
                .nowBusStopId(info.getNowBusStopId())
                .busstopName(info.getBusstopName())
                .shortLineName(info.getShortLineName())
                .remainMin(dto.getOperationTime())
                .lineId(info.getLineId())
                .arriveFlag(info.getArriveFlag())
                .build();
    }

    private PredictTimeResponseDto sendToPythonServer(List<BusArrivalInfo> infos, String arrivalBusStopName) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        BusArrivalInfo busInfo = getWantBusInfo(infos);

        String data = objectMapper.writeValueAsString(createRequestDto(
                getWantStationList(routeApiManager.busStopList(
                        routeService.findOne(busInfo.getShortLineName()
                        )
                ), busInfo, arrivalBusStopName)
                , busInfo, arrivalBusStopName));
        log.info("Predict Time data = {}", data);

        HttpEntity<String> entity = new HttpEntity<>(data, headers);
        log.info("request entity : {}", entity);
        return restTemplate.postForObject(pythonURL, entity, PredictTimeResponseDto.class);
    }

    private List<RouteBusStopListResponse> getWantStationList(List<RouteBusStopListResponse> list, BusArrivalInfo busInfo, String arrivalBusStopName) {
        boolean checking = false;

        return makeStationList(list, busInfo, arrivalBusStopName, checking);
    }

    private List<RouteBusStopListResponse> makeStationList(List<RouteBusStopListResponse> list, BusArrivalInfo busInfo, String arrivalBusStopName, boolean checking) {
        List<RouteBusStopListResponse> result = new ArrayList<>();

        for (RouteBusStopListResponse busStop : list) {
            if (!checking) {
                if (busInfo.getNowBusStopId().equals(busStop.getBusStopId())) {
                    result.add(busStop);
                    checking = true;
                }
            } else {
                result.add(busStop);

                if (busStop.getBusStopName().equals(arrivalBusStopName)) {
                    break;
                }
            }
        }

        return result;
    }

    private BusArrivalInfo getWantBusInfo(List<BusArrivalInfo> infos) {
        BusArrivalInfo busInfo = new BusArrivalInfo();
        for (BusArrivalInfo info : infos) {
            if (info.getShortLineName().equals("진월07")) {
                busInfo = info;
            }
        }
        return busInfo;
    }

    private PredictTimeRequestDto createRequestDto(List<RouteBusStopListResponse> list, BusArrivalInfo info, String arrivalBusStopName) {
        return PredictTimeRequestDto.builder()
                .routeName(info.getShortLineName())
                .startBusStopName(info.getBusstopName())
                .arrivalBusStopName(arrivalBusStopName)
                .routeInfos(list)
                .build();
    }
}
