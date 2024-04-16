package capston.busthecall.manager;

import capston.busthecall.domain.dto.response.BusArrivalInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


@Component
public class OpenApiManager {
    private final String BASE_URL = "http://api.gwangju.go.kr/json/arriveInfo";

    // serviceKey를 클래스 필드로 선언.
    private final String serviceKeyParam;

    // 생성자를 통해 serviceKey 값 주입 및 serviceKeyParam 초기화
    public OpenApiManager(@Value("${api.serviceKey}") String serviceKey) {
        this.serviceKeyParam = "?ServiceKey=" + serviceKey;
    }

    // makeUrl 메소드 수정. @RequestParam 제거 및 파라미터명 stationId로 변경.
    private String makeUrl(Long stationId) throws UnsupportedEncodingException {

        return BASE_URL + serviceKeyParam + "&BUSSTOP_ID=" + stationId;
    }

    public List<BusArrivalInfo> fetch(Long stationId) throws UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(makeUrl(stationId), HttpMethod.GET, entity, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode busStopList = rootNode.path("BUSSTOP_LIST");

            List<BusArrivalInfo> busInfos = new ArrayList<>();
            if (busStopList.isArray()) {
                for (JsonNode node : busStopList) {
                    Long busId = node.path("BUS_ID").asLong();
                    int remainStop = node.path("REMAIN_STOP").asInt();
                    String busstopName = node.path("BUSSTOP_NAME").asText();
                    String shortLineName = node.path("SHORT_LINE_NAME").asText();
                    Long remainMin = node.path("REMAIN_MIN").asLong();
                    Long lineId = node.path("LINE_ID").asLong();

                    BusArrivalInfo busArrivalInfo = BusArrivalInfo.builder()
                            .busId(busId)
                            .remainStop(remainStop)
                            .busstopName(busstopName)
                            .shortLineName(shortLineName)
                            .remainMin(remainMin)
                            .lineId(lineId)
                            .build();

                    busInfos.add(busArrivalInfo);
                }
            }
            return busInfos;
        } catch (Exception e) {
            return null;
        }
    }
}
