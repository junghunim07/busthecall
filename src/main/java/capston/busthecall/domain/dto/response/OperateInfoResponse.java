package capston.busthecall.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperateInfoResponse {

    private Long busId;
    private Long routeId;
    private String routeName;
    private String busNumber;
}
