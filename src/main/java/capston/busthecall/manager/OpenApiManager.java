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
import java.util.Map;


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
        // serviceKeyParam 필드를 사용하여 URL 완성
        return BASE_URL + serviceKeyParam + "&BUSSTOP_ID=" + stationId; // stationId를 URL에 포함
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
                    Long busId = node.path("BUS_ID").asLong(); // String에서 Long으로 변경.
                    int remainStop = node.path("REMAIN_STOP").asInt();
                    String busstopName = node.path("BUSSTOP_NAME").asText();
                    String shortLineName = node.path("SHORT_LINE_NAME").asText(); // 추가된 필드
                    Long remainMin = node.path("REMAIN_MIN").asLong(); // 추가된 필드
                    Long lineId = node.path("LINE_ID").asLong(); // 추가된 필드

                    BusArrivalInfo busArrivalInfo = BusArrivalInfo.builder()
                            .busId(busId)
                            .remainStop(remainStop)
                            .busstopName(busstopName)
                            .shortLineName(shortLineName) // 빌더를 사용하여 추가된 필드 설정
                            .remainMin(remainMin)
                            .lineId(lineId)
                            .build();

                    busInfos.add(busArrivalInfo);
                }
            }

            // 여기에서 busInfos를 ResponseEntity로 감싸서 반환
            return busInfos;
        } catch (Exception e) {
            // 오류 처리
            return null;
        }
    }
}
