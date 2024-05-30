package capston.busthecall.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BusInfoDto {

    private Long routeId;
    private Long busId;
    private String busNumber;
}
