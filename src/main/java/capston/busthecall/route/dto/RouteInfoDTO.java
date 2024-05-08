package capston.busthecall.route.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteInfoDTO {

    private Long routeId;
    private String routeName;
    private int routeKind;
}
