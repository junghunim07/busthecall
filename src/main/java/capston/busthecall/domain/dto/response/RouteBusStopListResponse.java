package capston.busthecall.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RouteBusStopListResponse {

    private Long busStopId;
    private String busStopName;
    private int busStopKind;
}
