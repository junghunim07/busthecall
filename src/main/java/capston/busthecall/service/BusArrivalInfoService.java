package capston.busthecall.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
@Slf4j
public class BusArrivalInfoService {
    private final String BASE_URL = "http://api.gwangju.go.kr/json/arriveInfo";

    // serviceKey를 클래스 필드로 선언.
    private final String serviceKeyParam;

    // 생성자를 통해 serviceKey 값 주입 및 serviceKeyParam 초기화
    public BusArrivalInfoService(@Value("${api.serviceKey}") String serviceKey) {
        this.serviceKeyParam = "?ServiceKey=" + serviceKey;
    }

    // makeUrl 메소드 수정. @RequestParam 제거 및 파라미터명 stationId로 변경.
    private String makeUrl(Long stationId) throws UnsupportedEncodingException {
        // serviceKeyParam 필드를 사용하여 URL 완성
        return BASE_URL + serviceKeyParam + "&stationId=" + stationId; // stationId를 URL에 포함
    }

    public ResponseEntity<?> fetch(Long stationId) throws UnsupportedEncodingException
    {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        return restTemplate.exchange(makeUrl(stationId), HttpMethod.GET, entity, Map.class);
    }
}
