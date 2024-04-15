package capston.busthecall.Controller;

import capston.busthecall.domain.dto.response.BusArrivalInfo;
import capston.busthecall.manager.OpenApiManager;
import capston.busthecall.service.BusArrivalInfoService;
import capston.busthecall.support.ApiResponse;
import capston.busthecall.support.ApiResponseGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/list")
@Slf4j
public class BusArrivalInfoController {


    private final OpenApiManager openApiManager;

    @GetMapping("/{station_id}")
    public ApiResponse<ApiResponse.SuccessBody<List<BusArrivalInfo>>> getBusArrivalInfo(@PathVariable Long station_id) {
        try {
            // API로부터 버스 도착 정보를 가져옵니다.
            List<BusArrivalInfo> busArrivalInfos = openApiManager.fetch(station_id);
            // `ResponseEntity` 객체에서 `List<BusArrivalInfo>`를 추출합니다.

            // 성공적으로 정보를 가져왔다면, ApiResponse를 통해 성공 메시지와 함께 데이터를 반환합니다.
            return ApiResponseGenerator.success(busArrivalInfos, HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding error", e);
            // 인코딩 에러 발생 시, ApiResponse를 통해 실패 메시지를 반환합니다.
            return null;
        } catch (Exception e) {
            log.error("Error fetching bus arrival information", e);
            // 그외 에러 발생 시, ApiResponse를 통해 실패 메시지를 반환합니다.
            return null;
        }
    }

}
