package capston.busthecall.manager;

import capston.busthecall.domain.dto.response.BusArrivalInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class OpenApiManager {

    private final String BASE_URL = "http://api.gwangju.go.kr/json/arriveInfo";
    private final String serviceKeyParam;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenApiManager(@Value("${api.serviceKey}") String serviceKey, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.serviceKeyParam = "?ServiceKey=" + serviceKey;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }

    private String makeUrl(Long stationId) {
        return BASE_URL + serviceKeyParam + "&BUSSTOP_ID=" + stationId;
    }

    public List<BusArrivalInfo> fetch(Long stationId) {
        try {
            String url = makeUrl(stationId);
            HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return parseBusStopList(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching bus arrival information", e);
        }
    }

    private List<BusArrivalInfo> parseBusStopList(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode busStopList = rootNode.path("BUSSTOP_LIST");

            return StreamSupport.stream(busStopList.spliterator(), false)
                    .map(this::parseBusArrivalInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing bus stop list", e);
        }
    }

    private BusArrivalInfo parseBusArrivalInfo(JsonNode node) {
        String routeName = node.path("SHORT_LINE_NAME").asText();

        if (routeName.equals("진월07")) {
            return BusArrivalInfo.builder()
                    .busId(node.path("BUS_ID").asLong())
                    .remainStop(node.path("REMAIN_STOP").asInt())
                    .nowBusStopId(node.path("CURR_STOP_ID").asLong())
                    .busstopName(node.path("BUSSTOP_NAME").asText())
                    .shortLineName(routeName)
                    .remainMin(node.path("REMAIN_MIN").asLong() * 60)
                    .lineId(node.path("LINE_ID").asLong())
                    .arriveFlag(node.path("ARRIVE_FLAG").asInt())
                    .build();
        }
        return BusArrivalInfo.builder()
                .busId(node.path("BUS_ID").asLong())
                .remainStop(node.path("REMAIN_STOP").asInt())
                .busstopName(node.path("BUSSTOP_NAME").asText())
                .shortLineName(node.path("SHORT_LINE_NAME").asText())
                .remainMin(node.path("REMAIN_MIN").asLong())
                .lineId(node.path("LINE_ID").asLong())
                .arriveFlag(node.path("ARRIVE_FLAG").asInt())
                .build();
    }
}
