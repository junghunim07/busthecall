package capston.busthecall.route;

import capston.busthecall.domain.dto.response.BusArrivalInfo;
import capston.busthecall.exception.AppException;
import capston.busthecall.exception.ErrorCode;
import capston.busthecall.route.dto.RouteInfoDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class RouteApiManager {

    private final String SERVICE_URL_ROUTE_INFO = "http://api.gwangju.go.kr/json/lineInfo";
    private final String SERVICE_URL_ROUTE_BUS_STOP_LIST_INFO = "http://api.gwangju.go.kr/json/lineStationInfo";
    private final String serviceKey;

    public RouteApiManager(@Value("${route.api.serviceKey}") String serviceKey) {
        this.serviceKey = "?serviceKey=" + serviceKey;
    }

    public List<RouteInfoDTO> fetch() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(SERVICE_URL_ROUTE_INFO + serviceKey, HttpMethod.GET, entity, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode routeList = rootNode.path("LINE_LIST");

            List<RouteInfoDTO> routeInfoList = new ArrayList<>();
            if (routeList.isArray()) {
                for (JsonNode node : routeList) {
                    Long routeId = node.path("LINE_ID").asLong();
                    String routeName = node.path("LINE_NAME").asText();
                    int routeKind = node.path("LINE_KIND").asInt();

                    RouteInfoDTO routeInfoDTO = RouteInfoDTO.builder()
                            .routeId(routeId)
                            .routeName(routeName)
                            .routeKind(routeKind)
                            .build();

                    routeInfoList.add(routeInfoDTO);
                }
            }
            return routeInfoList;
        } catch (Exception e) {
            throw new AppException(ErrorCode.ROUTE_ENCODING_ERROR, "인코딩 에러");
        }
    }

    public List<String> busStopList(Long routeId) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(SERVICE_URL_ROUTE_BUS_STOP_LIST_INFO + serviceKey + "&LINE_ID=" + routeId, HttpMethod.GET, entity, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode busStopListInRoute = rootNode.path("BUSSTOP_LIST");

            List<String> busStopInfoListInRoute = new ArrayList<>();
            if (busStopListInRoute.isArray()) {
                for (JsonNode node : busStopListInRoute) {
                    busStopInfoListInRoute.add(node.path("BUSSTOP_NAME").asText());
                }
            }
            return busStopInfoListInRoute;
        } catch (Exception e) {
            throw new AppException(ErrorCode.ROUTE_ENCODING_ERROR, "인코딩 에러");
        }
    }
}
