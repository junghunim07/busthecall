package capston.busthecall.manager;

import capston.busthecall.exception.AppException;
import capston.busthecall.exception.ErrorCode;
import capston.busthecall.manager.dto.BusInfoDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BusApiManager {

    private final String API_URL = "http://api.gwangju.go.kr/json/busLocationInfo";

    public List<BusInfoDto> getBusNumber(Long routeId) {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(makeURL(routeId), HttpMethod.GET, entity, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode busLocationNode = rootNode.path("BUSLOCATION_LIST");
            List<BusInfoDto> busInfoList = new ArrayList<>();

            if (busLocationNode.isArray()) {
                for (JsonNode node : busLocationNode) {
                    Long lineId = node.path("LINE_ID").asLong();
                    Long busId = node.path("BUS_ID").asLong();
                    String busNumber = node.path("CARNO").asText();

                    busInfoList.add(BusInfoDto.builder()
                            .routeId(lineId)
                            .busId(busId)
                            .busNumber(busNumber)
                            .build());
                }
            }
            return busInfoList;
        } catch (Exception e) {
            throw new AppException(ErrorCode.BUS_ENCODING_ERROR, "인코딩 에러");
        }
    }

    private String makeURL(Long routeId) {
        return API_URL + "?LINE_ID=" + routeId;
    }
}
