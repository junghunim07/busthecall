package capston.busthecall.domain.dto.request;

import capston.busthecall.domain.dto.response.RouteBusStopListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictTimeRequestDto {

    private String routeName;
    private String startBusStopName;
    private String arrivalBusStopName;
    private List<RouteBusStopListResponse> routeInfos;
}
